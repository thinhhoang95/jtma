package RECUIT;

import java.util.*;
import java.io.*;

import OPERATIONS.Constantes;
import OPERATIONS.Operations;
import OPERATIONS.AIRSPACE.*;
import OPERATIONS.FLIGHT.*;

public class Etat {

    public int dimEtat;
    public Decision[] vecteur;
    public FeaturesEtat featuresEtat;
    public double maxCout,meanCout;
    private static Random generateur = new Random(122);
    public Decision oldDecision = new Decision();

    public Etat() {
	dimEtat = Operations.DIMENSION;
	vecteur = new Decision[dimEtat];
	featuresEtat=new FeaturesEtat();
	for (int i = 0; i < dimEtat; i++) {
	    vecteur[i] = new Decision(i);
	    vecteur[i].decisionNumber = i;
	}
    }


    public void putDecisionInOperation() {

	for (int i = 0; i < dimEtat; i++) {
	    Operations.putDecision(vecteur[i]);
	}
    }
	



    /*******************************************************************************************************************/
    /* preProcessing */
    /* This method implement the problem dependent preprocessing step */
    /* The dimension of the state space is produced by this method */
    /*******************************************************************************************************************/
    public static void preProcessing() {
	Operations.preProcessing();
    }


    /*******************************************************************************************************************/
    /* initAleatEtat */
    /* This methode initialize the state space attributs */
    /*******************************************************************************************************************/
 

    public void initEtat() {
	for (int i = 0; i < dimEtat; i++) {
	    vecteur[i].initDecision();
	    Operations.putDecision(vecteur[i]);
	}
    }

    public void initAleatEtat(int i) {
	vecteur[i].initAleatDecision();
	Operations.putDecision(vecteur[i]);

    }


    

	public String saveStateSpaceFeatures() {
		String buffer = featuresEtat.saveFeaturesEtat();
		return buffer;
	}



    public void calculMaxCritere() {
	maxCout = -10000000;
	featuresEtat.razFeaturesEtat();
	for (int i = 0; i < dimEtat; i++) {
	    Operations.evaluateDecisionInOperations(vecteur[i]);
	    featuresEtat.objective=featuresEtat.objective+vecteur[i].perfoDecision.objective;
	    featuresEtat.numConflicts=featuresEtat.numConflicts+vecteur[i].perfoDecision.numConflicts;
	    featuresEtat.evalNodes=featuresEtat.evalNodes+vecteur[i].perfoDecision.evalNodes;
	    
	    featuresEtat.evalLinks=featuresEtat.evalLinks+vecteur[i].perfoDecision.evalLinks;
	    featuresEtat.evalDelay=featuresEtat.evalDelay+vecteur[i].perfoDecision.evalDelay;
	    featuresEtat.evalSpeed=featuresEtat.evalSpeed+vecteur[i].perfoDecision.evalSpeed;
	    featuresEtat.evalRoute=featuresEtat.evalRoute+vecteur[i].perfoDecision.evalRoute;
	}
	
	for (int i = 0; i < dimEtat; i++) {
	    if (vecteur[i].y > maxCout) {
		maxCout = vecteur[i].y;
	    }
	}
	//calcul proba_mutation
	if (maxCout>0){
	    for (int i = 0; i < dimEtat; i++) {
		vecteur[i].probaMutation=0.1+0.9*vecteur[i].y/maxCout;
		
	    }
	}
    }


 
    

    public double evaluateDecision(int i) {
	double res;
	vecteur[i].perfoDecision.razPerfoDecision();
	Operations.evaluateDecisionInOperations(vecteur[i]);
	res = vecteur[i].y;
	return res;
    }

    public double probaMutation(int i) {
	double res;
	res = vecteur[i].probaMutation;
	return res;
    }
    
    public double perfoDecision(int i) {
	double res;
	res = vecteur[i].y;
	return res;
    }

    public void intensify(int i,double ratioTemperature){
	double oldY=vecteur[i].y;
	double newY;
	    for (int k=0;k<3;k++) {
		Operations.removeDecision(vecteur[i]);
		Decision.copy(vecteur[i], oldDecision);
		vecteur[i].changeDecision(ratioTemperature);
		Operations.putDecision(vecteur[i]);
		newY=evaluateDecision(i);
		if (newY>oldY) comeBack(i);
	    }
    }
    
    public void genererVoisinCool(int i,double ratioTemperature) {
	if (generateur.nextDouble()<ratioTemperature){
	    Operations.removeDecision(vecteur[i]);
	    Decision.copy(vecteur[i], oldDecision);
	    vecteur[i].changeDecision(ratioTemperature);
	    Operations.putDecision(vecteur[i]);
	}else {
	    intensify(i,ratioTemperature);
	}
    }

    public void genererVoisinHeat(int i,double ratioTemperature) {
	Operations.removeDecision(vecteur[i]);
	Decision.copy(vecteur[i], oldDecision);
	vecteur[i].changeDecision(ratioTemperature);
	Operations.putDecision(vecteur[i]);
    }

    
    public void comeBack(int i) {
	Operations.removeDecision(vecteur[i]);
	Decision.copy(oldDecision, vecteur[i]);
	Operations.putDecision(vecteur[i]);
    }
    
    /*******************************************************************************************************************/
    /* Printing and storage */
    /*******************************************************************************************************************/



    public String printOverallDecision() {
	String buffer = "";
	buffer = buffer + "Overall Decision \n";
	for (int i = 0; i < dimEtat; i++) {
	    buffer = buffer + vecteur[i].afficherDecision() + " \n ";
	}
	return buffer;
    }

    public void sauvegarderEtat(String nom) {

	String dateTimeFolder = "RESULT/" + GlobalSettings.TIMESTAMP_STRING;
	new File(dateTimeFolder).mkdirs();
	String fileOut = dateTimeFolder + "/" + nom + "_DECISION.res";

	try {
	    /* creation et ouverture des fichiers de sortie */
	    File out = new File(fileOut);
	    FileOutputStream fs = new FileOutputStream(out);
	    PrintWriter pw = new PrintWriter(fs);
	    for (int i = 0; i < dimEtat; i++) {
		pw.println(vecteur[i].afficherDecision());
	    }
	    pw.close();
	} catch (IOException ioe) {
	    System.err.println("Erreur fichier sortie sauvegarderEtat Etat");
	}
    }

    public void sauvegarderVolsInfo(String nom) {
		String dateTimeFolder = "RESULT/" + GlobalSettings.TIMESTAMP_STRING;
		new File(dateTimeFolder).mkdirs();
		String fileOut = dateTimeFolder + "/" + nom + "_FLIGHT.res";

	try {
	    /* creation et ouverture des fichiers de sortie */
	    File out = new File(fileOut);
	    FileOutputStream fs = new FileOutputStream(out);
	    PrintWriter pw = new PrintWriter(fs);
	    for (int i = 0; i < dimEtat; i++) {
		pw.println(FlightSet.tableFlight[vecteur[i].flightNumber].afficherFlightsInfo(vecteur[i]));
	    }
	    pw.close();
	} catch (IOException ioe) {
	    System.err.println("Erreur fichier sortie sauvegarderVolsInfo Etat");
	}
    }


   public void sauvegarderRwyInfo(String nom) {
		String dateTimeFolder = "RESULT/" + GlobalSettings.TIMESTAMP_STRING;
		new File(dateTimeFolder).mkdirs();
		String fileOut = dateTimeFolder + "/" + nom + "_RWY.res";
	double tMean;
	try {
	    /* creation et ouverture des fichiers de sortie */
	    File out = new File(fileOut);
	    FileOutputStream fs = new FileOutputStream(out);
	    PrintWriter pw = new PrintWriter(fs);
	    for (int i=0;i<Reseau.nbNodesSTAR;i++){
		if (Reseau.tableNodeSTAR[i].isRwy==1){
		    pw.print((Reseau.tableNodeSTAR[i].nodeNumber+1)+ " ");
		    for (Double key : Reseau.tableNodeSTAR[i].tableNodeEvent.keySet()){
			EventNode event=Reseau.tableNodeSTAR[i].tableNodeEvent.get(key);
			tMean=(event.tIn+event.tOut)/2;
			pw.print(event.flightNumber + " "+tMean+ " ");
		    }
		    pw.println();
		}
	    }
	    pw.close();
	} catch (IOException ioe) {
	    System.err.println("Erreur fichier sortie sauvegarderVolsInfo Etat");
	}
    }



    
    /*******************************************************************************************************************/

    public boolean isNotGlobalOptimum() {
	return (maxCout==0);
    }


    
    public void postProcessing() {
	calculMaxCritere();
	sauvegarderEtat(Constantes.NOM_RESULT);
	sauvegarderVolsInfo(Constantes.NOM_RESULT);
	sauvegarderRwyInfo(Constantes.NOM_RESULT);
    }

}
