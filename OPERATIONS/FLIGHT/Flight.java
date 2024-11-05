package OPERATIONS.FLIGHT;

import RECUIT.*;
import OPERATIONS.AIRSPACE.*;
import OPERATIONS.*;

public class Flight {
 	
    public int flightNumber;
    public String callsign;
    public int entryNumber=-1;
    public double entryTime;
    public double speedIn;
    public int wTCat;
    public double landingTime;

    private double tAbsCurrent;
    private double currentSpeed;

   
    //ARRIVALS
    public Flight(int flightNumber, String callsign, int entryNumber, double entryTime, double speedIn, int wTCat){
    	this.flightNumber = flightNumber;
	this.callsign = callsign;
    	this.entryNumber = entryNumber;
    	this.entryTime = entryTime;
    	this.speedIn = speedIn;
    	this.wTCat = wTCat;
	this.landingTime = calculLandingTime(0,0);
    }
    
    
    public void afficherFlight(){
	    System.out.println("\n Callsign = " + callsign + "\n Numero Avion = " + flightNumber+"\n Entry Number= " + entryNumber + "\n Entry time= " + entryTime + "\n SpeedIn= " + speedIn + "\n wTCat= " + wTCat+ "\n Initial Landing time= " + landingTime);
    }
    

    public void updateLandingTrack(Decision uneDecision, boolean put) {

	double tOri, tInOri, tOutOri;
	double tDest,tInDest, tOutDest;
	double tInRunway, tOutRunway;
	double travelTime, currentTime;
	int nodeOri,nodeDest;
       		
	//int routeNumber = uneDecision.routeNumber;
	int nbPourcentSpeed = uneDecision.nbPourcentSpeed;
	int nbSlotDeltaT = uneDecision.nbSlotsRTA;
	int routeNumber= uneDecision.routeNumber;

        int decisionNumber = uneDecision.decisionNumber;	
	
	double deltaT = Constantes.TIME_SLOT * (double) nbSlotDeltaT;
	double coeffSpeed = (double) nbPourcentSpeed / 100.0;

	/******************************************/
	double firstPourcentInMergeLeg=uneDecision.firstPourcentInMergeLeg;
	double secondPourcentInMergeLeg=uneDecision.secondPourcentInMergeLeg;
	boolean leftRight=uneDecision.leftRight;
	double pourcentInShortCut=uneDecision.pourcentInShortCut;
	double lengthOnRing,length0nShortCut;		      
	/***************************************************/
	
	Route uneRoute = Reseau.tableRouteSetSTAR[entryNumber].tableRoute[routeNumber];
	int currentLink = uneRoute.tableIndexLink[0];

	tAbsCurrent = entryTime + deltaT;
	currentSpeed = speedIn * (1.0 + coeffSpeed);
	currentTime = tAbsCurrent;
	flightNumber = uneDecision.flightNumber;	
	

	for (int i = 0; i < uneRoute.size; i++) {

	    currentLink = uneRoute.tableIndexLink[i];
	    nodeOri = Reseau.tableLinkSTAR[currentLink].ori;

	    tOri = currentTime;
	    tInOri = tOri - (Constantes.NORMH_TO_METER / currentSpeed); //NORMN: norme horizontale en nautique = 3
	    tOutOri = tOri + (Constantes.NORMH_TO_METER / currentSpeed);
	    EventNode eventNodeOri = new EventNode(flightNumber, decisionNumber, tInOri, tOutOri);
	    Reseau.tableNodeSTAR[nodeOri].updateEventNode(eventNodeOri, put);

	    
	    //adaptation for YUYANG*********************************************
	    travelTime = (Reseau.tableLinkSTAR[currentLink].length)/currentSpeed;
	    
	    if (Reseau.tableLinkSTAR[currentLink].type==1) {
	     lengthOnRing=Reseau.tableLinkSTAR[currentLink].firstPmsExtraDistance*(firstPourcentInMergeLeg);
	     travelTime=travelTime+(lengthOnRing)/currentSpeed;
	    }
	    if (Reseau.tableLinkSTAR[currentLink].type==2) {
		if (leftRight){
		    lengthOnRing=Reseau.tableLinkSTAR[currentLink].firstPmsExtraDistance*(firstPourcentInMergeLeg);
		}else {
		    lengthOnRing=Reseau.tableLinkSTAR[currentLink].secondPmsExtraDistance*(secondPourcentInMergeLeg);
		}
		 travelTime=travelTime+(lengthOnRing)/currentSpeed;
	    }
	    if (Reseau.tableLinkSTAR[currentLink].type==3) {
		length0nShortCut=Reseau.tableLinkSTAR[currentLink].length*pourcentInShortCut;
		travelTime=travelTime+(length0nShortCut)/currentSpeed;
	    }
	    //*******************************************************************
	    
	    tDest = currentTime + travelTime;
	    EventLink eventLinkIn = new EventLink(flightNumber, decisionNumber, wTCat, tOri, tDest, currentSpeed);
	    EventLink eventLinkOut = new EventLink(flightNumber, decisionNumber, wTCat, tOri, tDest, currentSpeed);
	    Reseau.tableLinkSTAR[currentLink].updateEventLinkIn(eventLinkIn, put);
	    Reseau.tableLinkSTAR[currentLink].updateEventLinkOut(eventLinkOut, put);

	    currentTime = currentTime + travelTime;
	}
	if (uneRoute.size>0) {
	    //mise a jour du dernier noeud
	    nodeDest=Reseau.tableLinkSTAR[currentLink].dest;
	    tDest=currentTime;
	    tInDest=tDest-(Constantes.NORMH_TO_METER/currentSpeed);
	    tOutDest=tDest+(Constantes.NORMH_TO_METER/currentSpeed);
	    EventNode eventNodeDest= new EventNode(flightNumber,decisionNumber,tInDest,tOutDest);
	    Reseau.tableNodeSTAR[nodeDest].updateEventNode(eventNodeDest,put);
	}	
	
    }

	private String getFullRoute(Link[] tableLinkSTAR, Route uneRoute){
		StringBuilder sb = new StringBuilder();
		int currentLink = uneRoute.tableIndexLink[0];
		
		for (int i = 0; i < uneRoute.size; i++) {
			currentLink = uneRoute.tableIndexLink[i];
			int nodeOri = Reseau.tableLinkSTAR[currentLink].ori;
			sb.append(" ").append(nodeOri);
		}

		// For the last node, append the destination node
		int nodeDest = Reseau.tableLinkSTAR[currentLink].dest;
		sb.append(" ").append(nodeDest);
		return sb.toString();
	}


    public void evaluateLandingTrack(Decision uneDecision) {

	double tOri, tInOri, tOutOri;
	double tDest,tInDest, tOutDest;
	double tInRunway, tOutRunway;
	double travelTime, currentTime;
	int nodeOri,nodeDest;

	double evalNodes=0.0,evalLinks=0.0;
	double evalRoute;
	int orderConflicts=0;
	int nbConflictsNode=0,nbConflictsLink=0;
       		
	//int routeNumber = uneDecision.routeNumber;
	int nbPourcentSpeed = uneDecision.nbPourcentSpeed;
	int nbSlotDeltaT = uneDecision.nbSlotsRTA;
	int routeNumber= uneDecision.routeNumber;
	
        int decisionNumber = uneDecision.decisionNumber;	
	
	double deltaT = Constantes.TIME_SLOT * (double) nbSlotDeltaT;
	double coeffSpeed = (double) nbPourcentSpeed / 100.0;
	
	/******************************************/
	double firstPourcentInMergeLeg=uneDecision.firstPourcentInMergeLeg;
	double secondPourcentInMergeLeg=uneDecision.secondPourcentInMergeLeg;
	boolean leftRight=uneDecision.leftRight;
	double pourcentInShortCut=uneDecision.pourcentInShortCut;
	double lengthOnRing,length0nShortCut;		      
	/***************************************************/
	
	Route uneRoute = Reseau.tableRouteSetSTAR[entryNumber].tableRoute[routeNumber];
	evalRoute=uneRoute.routeExtension;
	
	int currentLink = uneRoute.tableIndexLink[0];

	tAbsCurrent = entryTime + deltaT;
	currentSpeed = speedIn * (1.0 + coeffSpeed);
	currentTime = tAbsCurrent;
	flightNumber = uneDecision.flightNumber;	
	

	for (int i = 0; i < uneRoute.size; i++) {
	    // Iterate through each link (segment) of the flight route

	    currentLink = uneRoute.tableIndexLink[i];
	    nodeOri = Reseau.tableLinkSTAR[currentLink].ori;

		// Calculate entry and exit times for the origin node of the link
	    tOri = currentTime;
	    tInOri = tOri - (Constantes.NORMH_TO_METER / currentSpeed);
	    tOutOri = tOri + (Constantes.NORMH_TO_METER / currentSpeed);


	    // Create and evaluate an EventNode for the origin, updating conflict counts
	    EventNode eventNodeOri = new EventNode(flightNumber, decisionNumber, tInOri, tOutOri);
	    evalNodes=evalNodes+ Reseau.tableNodeSTAR[nodeOri].evaluateEventNode(eventNodeOri);
	    nbConflictsNode=nbConflictsNode+eventNodeOri.nbConflictsNode;



	    // Calculate initial travel time for the current link
	    travelTime = (Reseau.tableLinkSTAR[currentLink].length)/currentSpeed;

		String linkType = Reseau.tableLinkSTAR[currentLink].type==0 ? "normal" : Reseau.tableLinkSTAR[currentLink].type==1 ? "pms" : Reseau.tableLinkSTAR[currentLink].type==2 ? "merge" : "shortcut";
	    
		// Adjust travel time based on link type
		double timeOnPMSRing = 0.0;
	    if (Reseau.tableLinkSTAR[currentLink].type==1) {
			// Type 1: Add extra distance for a "ring" segment
			lengthOnRing=Reseau.tableLinkSTAR[currentLink].firstPmsExtraDistance*(firstPourcentInMergeLeg);
			travelTime=travelTime+(lengthOnRing)/currentSpeed;
			timeOnPMSRing = (lengthOnRing)/currentSpeed;
	    }

	    if (Reseau.tableLinkSTAR[currentLink].type==2) {
			// Type 2: Add extra distance for a "merge" segment, considering left or right path
			if (leftRight){
				lengthOnRing=Reseau.tableLinkSTAR[currentLink].firstPmsExtraDistance*(firstPourcentInMergeLeg);
			}else {
				lengthOnRing=Reseau.tableLinkSTAR[currentLink].secondPmsExtraDistance*(secondPourcentInMergeLeg);
			}
			travelTime=travelTime+(lengthOnRing)/currentSpeed;
			timeOnPMSRing = (lengthOnRing)/currentSpeed;
	    }
		
	    if (Reseau.tableLinkSTAR[currentLink].type==3) {
			// Type 3: Adjust for a "shortcut" segment
			length0nShortCut=Reseau.tableLinkSTAR[currentLink].length*pourcentInShortCut;
			travelTime=travelTime+(length0nShortCut)/currentSpeed;
	    }
	    //*******************************************************************

		// Calculate destination time based on origin time and travel time
	    tDest = currentTime + travelTime;

	    // Evaluate the EventLinks for conflicts and update evaluation scores
	    EventLink eventLinkIn = new EventLink(flightNumber, decisionNumber, wTCat, tOri, tDest, currentSpeed);
	    EventLink eventLinkOut = new EventLink(flightNumber, decisionNumber, wTCat, tOri,tDest, currentSpeed);
	    evalLinks=evalLinks+Reseau.tableLinkSTAR[currentLink].evaluateEventLinkIn(eventLinkIn);
	    evalLinks=evalLinks+Reseau.tableLinkSTAR[currentLink].evaluateEventLinkOut(eventLinkOut);
	    // Evaluate order conflicts between entry and exit events
		orderConflicts=Reseau.tableLinkSTAR[currentLink].evaluateEventLinkInOut(eventLinkIn,eventLinkOut);

	    evalLinks= evalLinks+(double)orderConflicts;

	    // Update total conflict count for links
	    nbConflictsLink=nbConflictsLink+eventLinkIn.nbConflictsLink+orderConflicts;

	    // Move to the next time step
	    currentTime = currentTime + travelTime;


		// For debugging: write to the link travel time log file
		// tOri, tDest, travelTime, linkType
		nodeDest=Reseau.tableLinkSTAR[currentLink].dest;
		RECUIT.GlobalSettings.writeToLinkTravelTimeLogFile("***");
		RECUIT.GlobalSettings.writeToLinkTravelTimeLogFile(RECUIT.GlobalSettings.showFlightInfo(flightNumber));
		RECUIT.GlobalSettings.writeToLinkTravelTimeLogFile("route=" + getFullRoute(Reseau.tableLinkSTAR, uneRoute));
		RECUIT.GlobalSettings.writeToLinkTravelTimeLogFile("tOri=" + tOri + " tDest=" + tDest + " travelTime=" + travelTime + " linkType=" + linkType + " nodeOri=" + nodeOri + " nodeDest=" + nodeDest + " timeOnPMSRing=" + timeOnPMSRing);
		RECUIT.GlobalSettings.writeToLinkTravelTimeLogFile("***");
	    
	}
	if (uneRoute.size>0) {
	    // Update the destination node and its evaluation
	    nodeDest=Reseau.tableLinkSTAR[currentLink].dest;
	    tDest=currentTime;
	    tInDest=tDest-(Constantes.NORMH_TO_METER/currentSpeed);
	    tOutDest=tDest+(Constantes.NORMH_TO_METER/currentSpeed);
	    
	    EventNode eventNodeDest= new EventNode(flightNumber,decisionNumber,tInDest,tOutDest);
	    evalNodes=evalNodes+ Reseau.tableNodeSTAR[nodeDest].evaluateEventNode(eventNodeDest);
	    nbConflictsNode=nbConflictsNode+eventNodeDest.nbConflictsNode;
	}
	
	uneDecision.perfoDecision.numConflicts=nbConflictsNode+nbConflictsLink;
	uneDecision.perfoDecision.evalNodes=evalNodes;
	uneDecision.perfoDecision.evalLinks=evalLinks;
	uneDecision.perfoDecision.evalDelay = Math.abs(deltaT/3600);//en heures
	uneDecision.perfoDecision.evalSpeed= Math.abs(coeffSpeed);
	uneDecision.perfoDecision.evalRoute= evalRoute;
	
	//attention  a changer*************************************5
	double iterProgress = RECUIT.GlobalSettings.getIterProgress();
	double weightForConflicts = 1000;
	double weightForDelay = 2;
	double exponentForConflicts = 0.01;
	if (RECUIT.GlobalSettings.getIsCooling()) {
		weightForConflicts = 1000;
		// weightForDelay = 1 * (1 + 9 * (1 - iterProgress)); // 1 to 10
		// exponentForConflicts increases from 0.01 to 0.1 exponentially
		// exponentForConflicts = 0.005 + 0.005 * (Math.exp((1 - iterProgress)) - 1) / (Math.E - 1);
	}
	uneDecision.perfoDecision.objective = weightForConflicts * (Math.exp(exponentForConflicts * uneDecision.perfoDecision.evalNodes)+ Math.exp(exponentForConflicts * uneDecision.perfoDecision.evalLinks))+ weightForDelay*(uneDecision.perfoDecision.evalDelay + uneDecision.perfoDecision.evalSpeed+ uneDecision.perfoDecision.evalRoute);
    }


    
    public void updateAircraftTrack(Decision uneDecision, boolean put) {
	    updateLandingTrack(uneDecision, put);
    }
    
    //calculate the landing time for constant deceleration movement
    
    public double calculLandingTime(int nbSlotDeltaT, int nbPourcentSpeed) {
	double tDest = 0.0;
	double finalSpeed;
	double travelTime = 0.0, currentTime, totalDistance = 0.0;

	double deltaT = Constantes.TIME_SLOT * (double) nbSlotDeltaT;
	double coeffSpeed = (double) nbPourcentSpeed / 100.0;
	currentSpeed = speedIn * (1.0 + coeffSpeed);
	currentTime = entryTime + deltaT;
	Route uneRoute = Reseau.tableRouteSetSTAR[entryNumber].tableRoute[0];

	int currentLink = uneRoute.tableIndexLink[0];

	for (int i = 0; i < uneRoute.size; i++) {
	    currentLink = uneRoute.tableIndexLink[i];
	    totalDistance = totalDistance + Reseau.tableLinkSTAR[currentLink].length;
	}
		
	travelTime = totalDistance / currentSpeed;
	tDest = currentTime + travelTime;

	return tDest;
	}

    //after optimization, write the flights info
    public String afficherFlightsInfo(Decision uneDecision){
	String res = "";
    	double pourcentSpeed = ((double)uneDecision.nbPourcentSpeed / 100.0);
	int speedInKT= (int)Math.round((1.0+pourcentSpeed) * speedIn * Constantes.MS_TO_KT);
	int entryTimeInSec= (int) (Constantes.TIME_SLOT * uneDecision.nbSlotsRTA + entryTime);

    	res = res + " " + callsign + " " + wTCat +" ";
 
	res = res + (entryNumber + 1) + " " + (uneDecision.routeNumber+1) + " "	+ entryTimeInSec +   " " + speedInKT;

	return res;
    }

    

}


