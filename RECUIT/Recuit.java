package RECUIT;

import OPERATIONS.*;
import OPERATIONS.AIRSPACE.*;
import java.util.*;
import java.io.*;

public class Recuit {
    private static Random generateur = new Random(123);

    /*******************************************************/
    /* Parametres du recuit */
    private static final int nbTransitions = 1000;
    private static final double alpha = 0.995;
    //private static final int nbTransitions = 500;
    //private static final double alpha = 0.99;
    private static final boolean minimisation = true;
    /*******************************************************/

    private Etat x;

    private File outFeatures;
    private FileOutputStream fsFeatures;
    private PrintWriter pwFeatures;
    
    private void openFiles(){
	String nomRes=Constantes.NOM_RESULT;
	String nomFileFeatures= "RESULT/"+nomRes+"_FEATURES.res";
	try {
	    outFeatures = new File(nomFileFeatures);
	    fsFeatures = new FileOutputStream(outFeatures);
			pwFeatures = new PrintWriter(fsFeatures);
		} catch (IOException ioe) {
			System.err.println("Erreur file opening");		
		}
	}


	private void closeFiles(){
		pwFeatures.close();
	}

	public void preProcessing() {
		Etat.preProcessing();
		x = new Etat();
		openFiles();
	}

	// principe d'acceptation en maximisation
	private boolean accept(double yi, double yj, double temp, boolean minimiser) {
		boolean res = false;
		double proba;

		// minimisation
		if (minimiser) {
			if (yj < yi) {
				res = true;
			} else {
				proba = Math.exp((yi - yj) / temp);
				if (generateur.nextDouble() < proba)
					res = true;
			}
		}
		// maximisation
		else {
			if (yj > yi) {
				res = true;
			} else {
				proba = Math.exp((yj - yi) / temp);
				if (generateur.nextDouble() < proba)
					res = true;
			}
		}
		return res;
	}

	// **************************************
	// Heat Up
	// *************************************
	public double heatUpLoop() { // HeatUp heat = new HeatUp();
		int acceptCount = 0;
		double yi, yj;
		double T = 0.01, tauxAccept = 0.0;
		int index=0;

		x.initEtat();
		do {
			acceptCount = 0;
			for (int i = 0; i < nbTransitions; i++) {
				// generation d'un point de l'espace d'etat
			
				yi = x.evaluateDecision(index);

				// generation d'un voisin
				x.genererVoisinHeat(index,1.0);
				yj = x.evaluateDecision(index);

				if (accept(yi, yj, T, minimisation)){
					acceptCount++;
				}
				index=(index+1)%(x.dimEtat);
			}
			tauxAccept = (double) acceptCount / (double) nbTransitions;
			System.out.println("T= " + T + " tauxAccept= " + tauxAccept);
			T = T * 1.1;
		} while (tauxAccept < 0.8);
		System.out.println("T= " + T + " tauxAccept= " + tauxAccept);
		return T;
	}


    
	
	// *****************************************
	// COOLING
	// *****************************************
    public void coolingLoop(double Tinit) { // HeatUp heat = new HeatUp();
	double yi = 0.0, yj = 0.0, proba;
	double T = Tinit;
	int index=0;
	boolean flag=false;
	double tirage;
	double ratioTemperature;
	x.calculMaxCritere();
	do {
	    ratioTemperature=T/Tinit;
	    for (int i = 0; i < nbTransitions; i++) {
		tirage =generateur.nextDouble();
		if (tirage<x.probaMutation(index)){
		    x.genererVoisinCool(index,ratioTemperature);
		    yj = x.evaluateDecision(index);
		    if (accept(yi, yj, T, minimisation)) {
			yi = yj;
		    } else {
			x.comeBack(index);
		    }
		}
		index=(index+1)%(x.dimEtat);
		if (index==0) {
		    x.calculMaxCritere();
		    System.out.println("T= " + T + " Worse Decision Objective " + x.maxCout);
		    System.out.println(" "+x.featuresEtat.afficherFeaturesEtat());
		    pwFeatures.print(T+" "+x.saveStateSpaceFeatures());
		    if (x.maxCout==0) flag=true;
		}
	    }
	     T = T * alpha;
	} while ((T > 0.5 * Tinit)&&(!flag));
	x.calculMaxCritere();
	System.out.println("T= " + T + " Worse Decision Objective " + x.maxCout);
	System.out.println(" "+x.featuresEtat.afficherFeaturesEtat());
	pwFeatures.print(T+" "+x.saveStateSpaceFeatures());


	// To debug the nature of the NODE CONFLICTS

	GlobalSettings.ENABLE_LOG = true;

	for (int i = 0; i < x.dimEtat; i++) {
		x.evaluateDecision(i); 
	}



	
    }

	public void recuit() {
		double temperature;
		System.out.println("***********************heatUp**********************");
		temperature = heatUpLoop();
		System.out.println("***********************Cooling*********************");
		System.out.println("***********************Link Start****");
		Reseau.evaluateAllLink();
		System.out.println("***********************Node Start****");
		Reseau.evaluateAllNode();
		
		coolingLoop(temperature);
		
		System.out.println("***********************Link  end****");
		Reseau.evaluateAllLink();
		System.out.println("***********************Node end****");
		Reseau.evaluateAllNode();
		
	}

	public void postProcessing() {
		x.postProcessing();
		closeFiles();
	}

	// *******************************************
	// MAIN
	// *******************************************
    public static void main(String args[]) {
	String flightFile=args[0];
	String airportName=args[1];
	// Initialize GlobalSettings
	GlobalSettings.initializeGlobalSettings(flightFile);

	// PMS entry numbers, like 0,1,2,3 (will be used in Decision.java)
	int[] entryWithPMS;
	if (args.length > 2) {
		String[] pmsEntries = args[2].split(",");
		entryWithPMS = new int[pmsEntries.length];
		for (int i = 0; i < pmsEntries.length; i++) {
			entryWithPMS[i] = Integer.parseInt(pmsEntries[i]);
		}
		System.out.println("PMS entry numbers: " + Arrays.toString(entryWithPMS));
	} else {
		entryWithPMS = new int[0];
		System.out.println("No PMS entry numbers provided, using default.");
	}
	GlobalSettings.setEntryWithPMS(entryWithPMS); // sync it to GlobalSettings so it can be used in Decision.java

	Constantes.NOM_FLIGHTSET=flightFile;
	Constantes.NOM_AEROPORT=airportName;
	Constantes.NOM_RESULT=airportName+flightFile;
	Recuit monRecuit = new Recuit();
	monRecuit.preProcessing();
	long a=System.currentTimeMillis();
	monRecuit.recuit();
	System.out.println("\r<br>computational time : "+(System.currentTimeMillis()-a)/1000f+" s ");
	monRecuit.postProcessing();

	// Execute python script to compile link events
	
	try {
		System.out.println("Executing link events script...");
		ProcessBuilder processBuilder = new ProcessBuilder("python", "Inspect/compile_link_events.py", GlobalSettings.LINK_TRAVEL_TIME_LOG_FILE_PATH);
		Process process = processBuilder.start();
		int exitCode = process.waitFor();
		if (exitCode == 0) {
			System.out.println("Link events script execution completed successfully.");
		} else {
			System.err.println("Link events script execution failed with exit code: " + exitCode);
		}
	} catch (Exception e) {
		System.err.println("Error executing Link events script: " + e.getMessage());
		e.printStackTrace();
	}
    }// end main
}// End class

