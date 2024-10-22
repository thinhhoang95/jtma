package OPERATIONS.FLIGHT;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import OPERATIONS.AIRSPACE.*;
import OPERATIONS.*;
/*
 * Reading datas of each flight : entry number,entry time,speed,category
 */
public class FlightSet {

    public static int nbFlights;
    public static Flight[] tableFlight;
    private static Random generateur=new Random(123);

    
    public static void readFlights(String nomGen) {

	String nomFicFlight =  nomGen + ".flights";
	String ligne;
	int i;
	
         int flightNumber;
	 String callsign;
	 String typeAvion;
	 int reCat;
	 int entryWP;
	 int entryNumber=-1;
	 double landingDuration;
	 double speedIn;
	 double entryTime;
	 int wTCat;
	 String arrRwy;
	 
      

	 try {
	    FileReader fileFlight = new FileReader(nomFicFlight);
	    BufferedReader fileInputFlight = new BufferedReader(fileFlight);

	    fileInputFlight.mark(1000000);
	    System.out.println("Flights reading ...");
	    nbFlights = 0;
	    //lecture de la ligne de commentaire
	    ligne = fileInputFlight.readLine();
	    while ((ligne = fileInputFlight.readLine()) != null) {
		nbFlights++;
		StringTokenizer tokenizer = new StringTokenizer(ligne);
	    }

	    tableFlight = new Flight[nbFlights];
	    
	    fileInputFlight.reset();
	    //lecture de la ligne de commentaire
	    ligne = fileInputFlight.readLine();
	    i = 0;
	    while ((ligne = fileInputFlight.readLine()) != null) {
		StringTokenizer tokenizer = new StringTokenizer(ligne);
		// System.out.println(ligne);
		flightNumber = i;
		callsign=tokenizer.nextToken();
		entryNumber=Integer.parseInt(tokenizer.nextToken())-1;
		entryTime=Double.parseDouble(tokenizer.nextToken())+generateur.nextDouble();
		speedIn= Constantes.KT_TO_MS * Double.parseDouble(tokenizer.nextToken());
		wTCat=Integer.parseInt(tokenizer.nextToken());// 0-Small 1-Medium 2-Large
		// typeAvion=tokenizer.nextToken();
		// wTCat=Integer.parseInt(tokenizer.nextToken());// 0-Small 1-Medium 2-Large
		// speedIn= Constantes.KT_TO_MS * Double.parseDouble(tokenizer.nextToken());
		
		tableFlight[i] = new Flight(flightNumber, callsign, entryNumber, entryTime, speedIn, wTCat);
		i++;
	    }
	    nbFlights = i;
	    
		// Copy the tableFlight to a new array
		Flight[] newTableFlight = new Flight[nbFlights];
		for (int j = 0; j < nbFlights; j++) {
			newTableFlight[j] = tableFlight[j];
		}
		tableFlight = newTableFlight;

		// Set GlobalSettings tableFlight
		RECUIT.GlobalSettings.setTableFlight(tableFlight);

	    fileInputFlight.close();

	} catch (IOException ioe) {
	    System.err.println("Flight File reading error");
		throw new RuntimeException(ioe);
	}
    }

       
    public void writeFlight(String nomGen,int rate)
	{

	    String nameFileFlight=nomGen+".flights";

        String callsign;
	    int entryNumber,entryTime,speedIn,wTCat,reCat;
		int range;
		boolean sign;
       
	    try {

		String ligne,buffer;
		int nbArcs,ori,dest,cpt=0;
		double x=0,y=0,charge,flotEntrant,flotSortant,flux;

		FileWriter fileWriterFlight = new FileWriter(nameFileFlight);
		PrintWriter fileOutputFlight = new PrintWriter(fileWriterFlight);

		System.out.println("Flight File building ...");
		

		for (int i=0;i<nbFlights;i++){
		    for (int j=0;j<rate;j++){
			callsign=tableFlight[i].callsign;
			if (generateur.nextDouble()<0.90){
			    if (j!=0) callsign=callsign+"_"+j;
			    entryNumber=tableFlight[i].entryNumber+1;

				range= 600 + generateur.nextInt(5*60);
				sign = generateur.nextBoolean();
			    if (sign) entryTime=(int)tableFlight[i].entryTime + generateur.nextInt(range);
				else entryTime= (int)tableFlight[i].entryTime - generateur.nextInt(range);
                wTCat=generateur.nextInt(2) + 1;

			    
			    speedIn = (int)(tableFlight[i].speedIn * Constantes.MS_TO_KT) + generateur.nextInt(10);
			    
			    fileOutputFlight.print(callsign+" " + entryNumber + " " +entryTime + " " + speedIn+ " " +  wTCat);
			    fileOutputFlight.println();
			}
		    }
			    
		}
		

		fileOutputFlight.close();
		}catch(IOException ioe) {
	    	System.err.println("Erreur fichier");
		}
	}

    public void reduceFlights(String originalFileName, String increasedFileName, double keepRatio) {
		try {
			List<String> originalFlights = Files.readAllLines(Paths.get(originalFileName + ".flights"));
			List<String> increasedFlights = Files.readAllLines(Paths.get(increasedFileName + ".flights"));
			
			int originalFlightCount = originalFlights.size();
			int targetFlightCount = (int) ((originalFlightCount-1) * keepRatio);
			
			Collections.shuffle(increasedFlights);
			List<String> keptFlights = increasedFlights.subList(0, targetFlightCount);
			
			Files.write(Paths.get(increasedFileName + "_reduced.flights"), keptFlights);
		} catch (IOException e) {
			System.err.println("Error reducing flights: " + e.getMessage());
		}
	}
 

    
    public static void main(String[] args) {
		String originalFileName = "DATA/SHEN_ZHEN/ZGSZ_PMS_eliminate0";
		String increasedFileName = originalFileName + "_120%";
		FlightSet flightSet = new FlightSet();
		Reseau.lireReseau("DATA/SHEN_ZHEN/ZGSZ_PMS");
		flightSet.readFlights(originalFileName);
		flightSet.writeFlight(increasedFileName, 2);
		flightSet.reduceFlights(originalFileName, increasedFileName, 1.2); // Keep ()% of original flights
	}
    

}


    
