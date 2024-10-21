package OPERATIONS;

import OPERATIONS.FLIGHT.*;
import OPERATIONS.AIRSPACE.*;
import RECUIT.*;

public class Operations {
    public static String nomAeroport = "DATA/SHEN_ZHEN/"+Constantes.NOM_AEROPORT;
    public static String nomFlight = "DATA/SHEN_ZHEN/"+Constantes.NOM_FLIGHTSET;
 
    
    private static final boolean REMOVE = false;
    private static final boolean PUT=true;
    public static int DIMENSION;
   
    public static void preProcessing(){
	
	//Reseau.lireReseau(nomAeroport);

	Reseau.readReseau(nomAeroport);
	//FlightSet.lireFlights(nomFlight);

	FlightSet.readFlights(nomFlight);
	
	DIMENSION=FlightSet.nbFlights;
    }

        
    public static void putDecision(Decision oneDecision){
	int flightNumber = oneDecision.flightNumber;
	FlightSet.tableFlight[flightNumber].updateAircraftTrack(oneDecision, PUT);
    }

    public static void removeDecision(Decision oneDecision){
	int flightNumber = oneDecision.flightNumber;
	FlightSet.tableFlight[flightNumber].updateAircraftTrack(oneDecision,REMOVE);
    }

    
    public static void evaluateDecisionInOperations(Decision oneDecision){
	int flightNumber = oneDecision.flightNumber;
	FlightSet.tableFlight[flightNumber].evaluateLandingTrack(oneDecision);
	oneDecision.y= oneDecision.perfoDecision.objective;
    }


}
