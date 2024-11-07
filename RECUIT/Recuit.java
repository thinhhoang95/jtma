package RECUIT;

import OPERATIONS.*;
import OPERATIONS.AIRSPACE.*;
import java.util.*;
import java.io.*;

public class Recuit {
    private static Random generateur = new Random(123);

    /*******************************************************/
    /* Parametres du recuit */
    private static int nbTransitions = 1000;
	private static double heatUntil = 0.9999;
	private static double tCutOffCoeff = 0.0005;
    //private static final int nbTransitions = 500;
    //private static final double alpha = 0.99;
    private static final boolean minimisation = true;
    /*******************************************************/
	/* Metropolis Adaptive */
	private double acceptanceCount = 0;
    private double totalCount = 0;
    private static final double TARGET_ACCEPTANCE_RATE = 0.44;  // Typically between 0.4 and 0.5
	/* Metropolis History */
	private double bestSoFar = Double.MAX_VALUE;  
	private static final double epsilon = 1e-10;  // Small constant to avoid division by zero
	/* Dynamic Cooling Rate Adjustment */
	private static double alpha = 0.999;  // Initial cooling rate
	private static final double MIN_ALPHA = 0.99;  // Minimum cooling rate
	private static final double MAX_ALPHA = 0.9999;  // Maximum cooling rate
	private static final double ALPHA_ADJUSTMENT = 1.0001;  // How much to adjust alpha

    private Etat x;

    private File outFeatures;
    private FileOutputStream fsFeatures;
    private PrintWriter pwFeatures;
    
    private void openFiles(){
	String nomRes=Constantes.NOM_RESULT;
	String nomFileFeatures= "RESULT/"+GlobalSettings.TIMESTAMP_STRING+"/"+nomRes+"_FEATURES.res";
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
				res = true; // accept the move
			} else {
				proba = Math.exp((yi - yj) / temp);
				if (generateur.nextDouble() < proba)
					res = true; // accept the move
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


	// principe d'acceptation en maximisation
	private boolean acceptAdaptiveMetropolis(double yi, double yj, double temp, boolean minimiser) {
		boolean res = false;
        double proba;

        if (minimiser) {
            if (yj < yi) {
                res = true;
            } else {
                double currentAcceptanceRate = acceptanceCount / Math.max(1.0, totalCount);  // Avoid division by zero
                double adaptiveFactor = currentAcceptanceRate / TARGET_ACCEPTANCE_RATE;
                proba = Math.exp(-adaptiveFactor * (yj - yi) / temp);
                if (generateur.nextDouble() < proba)
                    res = true;
            }
        }
        
        totalCount++;
        if (res) acceptanceCount++;
        
        return res;
	}

	private boolean acceptMetropolisHistory(double yi, double yj, double temp, boolean minimiser) {
		boolean res = false;
		double proba;
		
		// Keep track of the best solution seen so far
		if (minimiser) {
			if (yj < yi) {
				res = true;  // Always accept improvements
				bestSoFar = Math.min(bestSoFar, yj);  // Update best solution
			} else {
				// Calculate relative deterioration compared to best solution
				double delta = yj - yi;  // Absolute difference
				// Normalize the delta by how far we are from the best solution
				double relativeDelta = delta / (yi - bestSoFar + epsilon);
				
				// Use modified acceptance probability
				proba = Math.exp(-relativeDelta / temp);
				if (generateur.nextDouble() < proba)
					res = true;
			}
		} else {
			// For maximization problems (if needed)
			if (yj > yi) {
				res = true;
				bestSoFar = Math.max(bestSoFar, yj);
			} else {
				double delta = yi - yj;
				double relativeDelta = delta / (bestSoFar - yi + epsilon);
				proba = Math.exp(-relativeDelta / temp);
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
		double yi = 0.0, yj = 0.0;
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
			x.calculMaxCritere();
			System.out.print("\033[2K\rT= " + T + " J: " + x.featuresEtat.objective);
			T = T * 1.1;
			// T = T + 20;
		} while (tauxAccept < heatUntil);
		System.out.print("\rT= " + T + " J: " + yi);
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

	// Set the cooling flag
	GlobalSettings.setIsCooling(true);

	double tMin = tCutOffCoeff * Tinit;

	// Show before cooling status
	System.out.println("***********************Pre Cooling *********************");
	System.out.println("T= " + String.format("%.2f", T) + " => " + String.format("%.2f", tMin) + "; Worse Decision Objective " + x.maxCout);
	System.out.println(" "+x.featuresEtat.afficherFeaturesEtat());
	pwFeatures.print(T+" "+x.saveStateSpaceFeatures());
	System.out.println("***********************Cooling Started*********************");
	System.out.println("CF: Conflicts, CD: Conflicts Removed, CR: Relative Conflicts Removed, SI: Strict Improvement, WA: Worse Accepted");

	int iterOfLoop = 0; // to keep track of the conflict resolution rate
	int conflictAtBegin = (int) x.featuresEtat.numConflicts;

	int calculateRateEveryNIter = 50;
	int strictImprovementCount = 0;
	int worseAcceptCount = 0;

	int maxConflictsOfLoop = 0;
	int minConflictsOfLoop = 99999999;

	do {
	    ratioTemperature=T/Tinit;
	    for (int i = 0; i < nbTransitions; i++) {
		tirage =generateur.nextDouble();
		if (tirage<x.probaMutation(index)){
		    x.genererVoisinCool(index,ratioTemperature);
		    yj = x.evaluateDecision(index);
			if (iterOfLoop == 0) {
				yi = yj;
				continue;
			}
			if (minimisation && (yj < yi)) {
				strictImprovementCount++;
			}
			if (acceptAdaptiveMetropolis(yi, yj, T, minimisation)) {
				if (yi < yj) { // the new cost is larger than the old one
					worseAcceptCount++;
				}
				yi = yj;
		    } else {
				x.comeBack(index);
		    }
		}
		index=(index+1)%(x.dimEtat);
		if (index==0) {
		    x.calculMaxCritere();
			String featuresEtatString = x.featuresEtat.afficherFeaturesEtatConflitsSeuls();
			int numConflicts = (int) x.featuresEtat.numConflicts;
			double conflictDelta = conflictAtBegin - numConflicts;
			double conflictRate = conflictDelta / conflictAtBegin * 100.0;
			if (numConflicts > maxConflictsOfLoop) {
				maxConflictsOfLoop = numConflicts;
			}
			if (numConflicts < minConflictsOfLoop) {
				minConflictsOfLoop = numConflicts;
			}
		    System.out.print("\033[2K\rT= " + String.format("%.2f", T) + " => " + String.format("%.2f", tMin) + " | " + featuresEtatString + "(" + maxConflictsOfLoop + "/" + minConflictsOfLoop + ")" + " CD: " + String.format("%.2f", conflictDelta) + " CR: " + String.format("%.2f", conflictRate) + " SI: " + strictImprovementCount + " WA: " + worseAcceptCount);
			// ;// + " / " + String.format("%.2f", tMin) + " Iter Progress: " + String.format("%.2f", GlobalSettings.getIterProgress()) + "; Worse Decision Objective " + x.maxCout);
		    // System.out.print(" "+x.featuresEtat.afficherFeaturesEtat());
		    // System.out.flush();
		    // pwFeatures.print(T+" "+x.saveStateSpaceFeatures());
		    if (x.maxCout==0) flag=true;
		} // index==0 loop
	    } // transition loop
	     T = T * alpha;
	     GlobalSettings.setIterProgress((T - tMin) / (Tinit - tMin));

		iterOfLoop++;
		if (iterOfLoop%calculateRateEveryNIter==0) {
			System.out.println();
			// System.out.println("\nConResRate: " + (conflictAtBegin - conflictAtEnd) / (double)calculateRateEveryNIter + " (confs/iter)");
			// System.out.println("RelConResRate: " + (conflictAtBegin - conflictAtEnd) / (double)conflictAtBegin * 100.0 / (double)calculateRateEveryNIter + " (percent/iter)");
			// System.out.println("StrictImprovementCount: " + strictImprovementCount);
			// System.out.println("--------------------------------");
			conflictAtBegin = (int) x.featuresEtat.numConflicts;
			strictImprovementCount = 0;
			worseAcceptCount = 0;
			maxConflictsOfLoop = 0;
			minConflictsOfLoop = 99999999;
		}
	} while ((T > tMin)&&(!flag));
	// } while ((T > 1.0)&&(!flag));
	x.calculMaxCritere();
	System.out.println("***********************Cooling Ended*********************");
	System.out.println("T= " + String.format("%.2f", T) + " / " + String.format("%.2f", tMin) + "; Worse Decision Objective " + x.maxCout);
	System.out.println(" "+x.featuresEtat.afficherFeaturesEtat());
	pwFeatures.print(T+" "+x.saveStateSpaceFeatures());


	// To debug the nature of the NODE CONFLICTS

	GlobalSettings.ENABLE_LOG = true;

	for (int i = 0; i < x.dimEtat; i++) {
		x.evaluateDecision(i); 
	}



	
    }






	public void coolingLoopAdaptive(double Tinit) { // HeatUp heat = new HeatUp();
		double yi = 0.0, yj = 0.0, proba;
		double T = Tinit;
		int index=0;
		boolean flag=false;
		double tirage;
		double ratioTemperature;
		double currentAcceptanceRate = 0.0;
		x.calculMaxCritere();
	
		// Set the cooling flag
		GlobalSettings.setIsCooling(true);
	
		double tMin = tCutOffCoeff * Tinit;
	
		// Show before cooling status
		System.out.println("***********************Pre Cooling *********************");
		System.out.println("T= " + String.format("%.2f", T) + " => " + String.format("%.2f", tMin) + "; Worse Decision Objective " + x.maxCout);
		System.out.println(" "+x.featuresEtat.afficherFeaturesEtat());
		pwFeatures.print(T+" "+x.saveStateSpaceFeatures());
		System.out.println("***********************Cooling Started*********************");
		System.out.println("CF: Conflicts, CD: Conflicts Removed, CR: Relative Conflicts Removed, SI: Strict Improvement, WA: Worse Accepted");
	
		int iterOfLoop = 0; // to keep track of the conflict resolution rate
		int conflictAtBegin = (int) x.featuresEtat.numConflicts;
	
		int calculateRateEveryNIter = 50;
		int strictImprovementCount = 0;
		int worseAcceptCount = 0;
	
		int maxConflictsOfLoop = 0;
		int minConflictsOfLoop = 99999999;
	
		do {
			ratioTemperature=T/Tinit;
			int acceptedMoves = 0;
			int totalMoves = 0;

			for (int i = 0; i < nbTransitions; i++) {
			tirage =generateur.nextDouble();
			if (tirage<x.probaMutation(index)){
				x.genererVoisinCool(index,ratioTemperature);
				yj = x.evaluateDecision(index);
				if (iterOfLoop == 0) {
					yi = yj;
					continue;
				}
				if (minimisation && (yj < yi)) {
					strictImprovementCount++;
				}
				if (acceptAdaptiveMetropolis(yi, yj, T, minimisation)) {
					acceptedMoves++;
					if (yi < yj) { // the new cost is larger than the old one
						worseAcceptCount++;
					}
					yi = yj;
				} else {
					x.comeBack(index);
				}
				totalMoves++;
			}
			index=(index+1)%(x.dimEtat);
			if (index==0) {
				x.calculMaxCritere();
				String featuresEtatString = x.featuresEtat.afficherFeaturesEtatConflitsSeuls();
				int numConflicts = (int) x.featuresEtat.numConflicts;
				double conflictDelta = conflictAtBegin - numConflicts;
				double conflictRate = conflictDelta / conflictAtBegin * 100.0;
				if (numConflicts > maxConflictsOfLoop) {
					maxConflictsOfLoop = numConflicts;
				}
				if (numConflicts < minConflictsOfLoop) {
					minConflictsOfLoop = numConflicts;
				}
				System.out.print("\033[2K\rT= " + String.format("%.2f", T) + " => " + String.format("%.2f", tMin) + " | " + featuresEtatString + "(" + maxConflictsOfLoop + "/" + minConflictsOfLoop + ")" + " CD: " + String.format("%.2f", conflictDelta) + " CR: " + String.format("%.2f", conflictRate) + " SI: " + strictImprovementCount + " WA: " + worseAcceptCount + " AL: " + String.format("%.4f", alpha) + " AR: " + String.format("%.2f", currentAcceptanceRate));
				// ;// + " / " + String.format("%.2f", tMin) + " Iter Progress: " + String.format("%.2f", GlobalSettings.getIterProgress()) + "; Worse Decision Objective " + x.maxCout);
				// System.out.print(" "+x.featuresEtat.afficherFeaturesEtat());
				// System.out.flush();
				// pwFeatures.print(T+" "+x.saveStateSpaceFeatures());
				if (x.maxCout==0) flag=true;
			} // index==0 loop
			} // transition loop


			// Adapt cooling rate based on acceptance rate
			currentAcceptanceRate = (double) acceptedMoves / totalMoves;
			if (currentAcceptanceRate > TARGET_ACCEPTANCE_RATE) {
				// Accepting too much
				// Cooling too slowly, decrease alpha to cool faster
				alpha = Math.max(MIN_ALPHA, alpha / ALPHA_ADJUSTMENT);
			} else if (currentAcceptanceRate < TARGET_ACCEPTANCE_RATE) {
				// Accepting too little
				// Cooling too quickly, increase alpha to cool slower
				alpha = Math.min(MAX_ALPHA, alpha * ALPHA_ADJUSTMENT);
			}
	
			T = T * alpha;
			GlobalSettings.setIterProgress((T - tMin) / (Tinit - tMin));
	
			iterOfLoop++;
			if (iterOfLoop%calculateRateEveryNIter==0) {
				System.out.println();
				// System.out.println("\nConResRate: " + (conflictAtBegin - conflictAtEnd) / (double)calculateRateEveryNIter + " (confs/iter)");
				// System.out.println("RelConResRate: " + (conflictAtBegin - conflictAtEnd) / (double)conflictAtBegin * 100.0 / (double)calculateRateEveryNIter + " (percent/iter)");
				// System.out.println("StrictImprovementCount: " + strictImprovementCount);
				// System.out.println("--------------------------------");
				conflictAtBegin = (int) x.featuresEtat.numConflicts;
				strictImprovementCount = 0;
				worseAcceptCount = 0;
				maxConflictsOfLoop = 0;
				minConflictsOfLoop = 99999999;
			}
		} while ((T > tMin)&&(!flag));
		// } while ((T > 1.0)&&(!flag));
		x.calculMaxCritere();
		System.out.println("***********************Cooling Ended*********************");
		System.out.println("T= " + String.format("%.2f", T) + " / " + String.format("%.2f", tMin) + "; Worse Decision Objective " + x.maxCout);
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
		if (args[2].equals("XXX")) {
			entryWithPMS = new int[0];
			System.out.println("No PMS entry numbers provided, using default.");
		} else {
			String[] pmsEntries = args[2].split(",");
			entryWithPMS = new int[pmsEntries.length];
			for (int i = 0; i < pmsEntries.length; i++) {
				entryWithPMS[i] = Integer.parseInt(pmsEntries[i]);
			}
			System.out.println("PMS entry numbers: " + Arrays.toString(entryWithPMS));
		}
	} else {
		entryWithPMS = new int[0];
		System.out.println("No PMS entry numbers provided, using default.");
	}
	GlobalSettings.setEntryWithPMS(entryWithPMS); // sync it to GlobalSettings so it can be used in Decision.java


	// Load hyperparameters if the filename is specified as the third argument
	if (args.length > 3) {
		HyperReader hyperReader = new HyperReader();
		HyperParameters hyperParameters = hyperReader.readHyperparameters(args[3]);
		nbTransitions = hyperParameters.getNbTransitions();
		alpha = hyperParameters.getAlpha();
		heatUntil = hyperParameters.getHeatUntil();
		tCutOffCoeff = hyperParameters.getTCutOffCoeff();
	}

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

