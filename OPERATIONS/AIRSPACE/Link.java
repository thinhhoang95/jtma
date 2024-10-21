package OPERATIONS.AIRSPACE;

import java.util.*;
import OPERATIONS.*;
import OPERATIONS.FLIGHT.FlightSet;

public class Link {
    public int ori;
    public int dest;
    public int type;
    public int first_angle;
    public int second_angle;
    public double pourcentage;
    public int linkNumber;
    public double length;
    public double firstPmsExtraDistance;
    public double secondPmsExtraDistance;
    public double shortCutExtraDistance;
    
    
    public TreeMap<Double,EventLink> tableLinkEventIn=new TreeMap<Double,EventLink>();
    public TreeMap<Double,EventLink> tableLinkEventOut=new TreeMap<Double,EventLink>();

    public Link(int ori, int dest,int type,int first_angle,int second_angle,double pourcentage, double length,int linkNumber){
	this.ori=ori;
	this.dest=dest;
	this.type=type;
	this.first_angle=first_angle;
	this.second_angle=second_angle;
	this.pourcentage=pourcentage;
	this.length=length;
	this.linkNumber=linkNumber;
	firstPmsExtraDistance=length*first_angle*Constantes.DEGRE_TO_RADIAN;
	secondPmsExtraDistance=length*second_angle*Constantes.DEGRE_TO_RADIAN;
	shortCutExtraDistance=length*(pourcentage/100-1);
    }


    
    public void updateEventLinkIn(EventLink unEvent,boolean put){
	EventLink buffer;
	if (put) {
		
	    if (tableLinkEventIn.containsKey(new Double(unEvent.tIn))){
		System.out.println("Same key on link in $$$$$$$$$$$$$$$$$$$$$$$$$ " + unEvent.tIn + " " + unEvent.flightNumber); 
		buffer=tableLinkEventIn.get(new Double(unEvent.tIn));
		System.out.println("Key and aircraft number  $$$$$$$$$$$$$$$$$$$$$$$$$ " + buffer.tIn + " " + buffer.flightNumber); 
	    }
	    tableLinkEventIn.put(new Double(unEvent.tIn),unEvent);
	}
	else {
	    
	    buffer=tableLinkEventIn.remove(new Double(unEvent.tIn));
	    if (buffer==null) System.out.println("Key not in Treemap Link In for remove $$$$$$$$$$$$$$$$$$$$$$$$$ "+ unEvent.flightNumber+" "+unEvent.tIn);
	}
    }


    public void updateEventLinkOut(EventLink unEvent,boolean put){
	EventLink buffer;
	if (put) {
	    if (tableLinkEventOut.containsKey(new Double(unEvent.tOut)))
		System.out.println("Same key on link out $$$$$$$$$$$$$$$$$$$$$$$$$ " + unEvent.tOut + " " + unEvent.flightNumber); 

	    tableLinkEventOut.put(new Double(unEvent.tOut),unEvent);
	}
	else {
	    buffer=tableLinkEventOut.remove(new Double(unEvent.tOut));
	    if (buffer==null) System.out.println("Key not in Treemap Link Out for remove $$$$$$$$$$$$$$$$$$$$$$$$$ "+ unEvent.flightNumber+" "+unEvent.tOut);

	}
    }

    public void clearTableLinkEvent(){
	tableLinkEventIn.clear();
	tableLinkEventOut.clear();
    }


    public PerfoConflict computePerfoConflict(double deltaT, double speed, int catPrecedent, int catSuivant){
	PerfoConflict res=new PerfoConflict();
	double separation, minSep;

	separation=speed*deltaT;
	minSep=Constantes.TABLE_DISTANCE_SEPARATION[catPrecedent][catSuivant];
	if (separation<minSep)
	    {
		//conflit de rattrapage
		res.defautSeparation=res.defautSeparation+(minSep-separation)/minSep;
		res.inConflict=true;
	    }
	return res;
    }



    

    public double evaluateEventLinkIn(EventLink eventIn) {
	double result=0.0;
	double ecart,defaut=0.0;
	double deltaCurrent,deltaNext,deltaPrevious,maxOverlap;
	boolean flag;
	Double currentKey,nextKey,previousKey;
	EventLink currentEvent,nextEvent,previousEvent;
	double separation,defautSeparation=0.0;
	int catSuivant,catPrecedent;
	
	PerfoConflict perfoConflict;
	
	double minSep;
	double deltaT;
	
	int nbConflitsRattrapage=0;

	// Conflict checking at the link entry***************************************************************************************
	currentKey=new Double(eventIn.tIn);
	currentEvent=tableLinkEventIn.get(currentKey);
	if (currentEvent==null) System.out.println("Key not in Treemap Node for remove $$$$$$$$$$$$$$$$$$$$$$$$$ " +" flight="+ eventIn.flightNumber+" tIn="+eventIn.tIn+" link="+linkNumber);
	
	//recherche des conflits en aval
	previousKey=currentKey;
	nextKey=tableLinkEventIn.higherKey(currentKey);
	do {		
	    flag=false;  
	    if (nextKey!=null){
		nextEvent=tableLinkEventIn.get(nextKey);
		catPrecedent=currentEvent.cat;
		catSuivant=nextEvent.cat;
	
		deltaT=nextEvent.tOut-currentEvent.tOut;
		
		perfoConflict=computePerfoConflict(deltaT,currentEvent.speed,catPrecedent,catSuivant);
		if (perfoConflict.inConflict){
		    defautSeparation=defautSeparation+perfoConflict.defautSeparation;
		    nbConflitsRattrapage=nbConflitsRattrapage+1;
		    flag=true;
		}
		
		/*separation=currentEvent.speed*deltaT;
		minSep=Constantes.TABLE_SEPARATION[catPrecedent][catSuivant];
		if ((separation)<minSep)
		    {
			//conflit de rattrapage
		       
			defautSeparation=defautSeparation+(minSep-separation)/minSep;
			nbConflitsRattrapage=nbConflitsRattrapage+1;
			flag=true;
			}*/
		nextKey=tableLinkEventIn.higherKey(nextKey);
	    }
	}while (flag);
	
	//recherche des conflits en amont
	nextKey=currentKey;
	previousKey=tableLinkEventIn.lowerKey(currentKey);
	do {
	    flag=false;  
	    if (previousKey!=null){
		previousEvent=tableLinkEventIn.get(previousKey);
		catPrecedent=previousEvent.cat;
		catSuivant=currentEvent.cat;
		
		deltaT= currentEvent.tIn-previousEvent.tIn;

		perfoConflict=computePerfoConflict(deltaT,currentEvent.speed,catPrecedent,catSuivant);
		if (perfoConflict.inConflict){
		    defautSeparation=defautSeparation+perfoConflict.defautSeparation;
		    nbConflitsRattrapage=nbConflitsRattrapage+1;
		    flag=true;
		}
		
		/*separation=previousEvent.speed*deltaT;
		minSep=Constantes.TABLE_SEPARATION[catPrecedent][catSuivant];
		if ((separation)<minSep)
		    {
			//conflit de rattrapage
			
			defautSeparation=defautSeparation+(minSep-separation)/minSep; 
			nbConflitsRattrapage=nbConflitsRattrapage+1;
			flag=true;
		    }
		*/
		
		previousKey=tableLinkEventIn.lowerKey(previousKey);
	    }
	}while (flag);

	
	result=defautSeparation + nbConflitsRattrapage;
        eventIn.nbConflictsLink=nbConflitsRattrapage;

	
	return result;
	
    }

    



    public double evaluateEventLinkOut(EventLink eventOut) {
	double result=0.0;
	double ecart,defaut=0.0;
	double deltaCurrent,deltaNext,deltaPrevious,maxOverlap;
	int prevIn=0,nextIn=0,prevOut=0,nextOut=0;
	boolean flag;
	int cpt=0;
	Double currentKey,nextKey,previousKey;
	EventLink currentEvent,nextEvent,previousEvent;
	double separation,defautSeparation=0.0;
	int catSuivant,catPrecedent;

	PerfoConflict perfoConflict;
	double minSep;
	double deltaT;
	int nbConflitsRattrapage=0;
	
	// Conflict checking at the link exit***************************************************************************************
	currentKey=new Double(eventOut.tOut);
	currentEvent=tableLinkEventOut.get(currentKey);
	if (currentEvent==null) System.out.println("Key not in Treemap Node for remove $$$$$$$$$$$$$$$$$$$$$$$$$ " +" flight="+ eventOut.flightNumber+" tOut="+eventOut.tOut+" link="+linkNumber);
	//recherche des conflits en aval
	previousKey=currentKey;
	nextKey=tableLinkEventOut.higherKey(currentKey);
	do {
	    flag=false;  
	    if (nextKey!=null){
		nextEvent=tableLinkEventOut.get(nextKey);
		catPrecedent=currentEvent.cat;
		catSuivant=nextEvent.cat;
		
		deltaT=nextEvent.tOut-currentEvent.tOut;
		
		perfoConflict=computePerfoConflict(deltaT,currentEvent.speed,catPrecedent,catSuivant);
		if (perfoConflict.inConflict){
		    defautSeparation=defautSeparation+perfoConflict.defautSeparation;
		    nbConflitsRattrapage=nbConflitsRattrapage+1;
		    flag=true;
		}

		/*
		
		separation=currentEvent.speed*deltaT;
		minSep=Constantes.TABLE_SEPARATION[catPrecedent][catSuivant];
		if ((separation)<minSep)
		    {
			//conflit de rattrapage
			defautSeparation=defautSeparation+(minSep-separation)/minSep; 
			nbConflitsRattrapage=nbConflitsRattrapage+1;
			flag=true;
		    }
		*/
		
		nextKey=tableLinkEventOut.higherKey(nextKey);
	    }
	}while (flag);
	
	//recherche des conflits en amont
	nextKey=currentKey;
	previousKey=tableLinkEventOut.lowerKey(currentKey);
	do {
	    flag=false;  
	    if (previousKey!=null){
		previousEvent=tableLinkEventOut.get(previousKey);
		catPrecedent=previousEvent.cat;
		catSuivant=currentEvent.cat;
		
		
		deltaT= currentEvent.tIn-previousEvent.tIn;

		perfoConflict=computePerfoConflict(deltaT,currentEvent.speed,catPrecedent,catSuivant);
		if (perfoConflict.inConflict){
		    defautSeparation=defautSeparation+perfoConflict.defautSeparation;
		    nbConflitsRattrapage=nbConflitsRattrapage+1;
		    flag=true;
		}

		/*
		separation=previousEvent.speed*deltaT;
		minSep=Constantes.TABLE_SEPARATION[catPrecedent][catSuivant];
		if ((separation)<minSep)
		    {
			//conflit de rattrapage
			defautSeparation=defautSeparation+(minSep-separation)/minSep; 
			nbConflitsRattrapage=nbConflitsRattrapage+1;
			flag=true;
		    }
		*/
		
		previousKey=tableLinkEventOut.lowerKey(previousKey);
	    }
	}while (flag);
	result=defautSeparation + nbConflitsRattrapage;
	eventOut.nbConflictsLink=nbConflitsRattrapage;
	return result;
	
    }


    




    public int evaluateEventLinkInOut(EventLink eventIn, EventLink eventOut) {
	
	double ecart,defaut=0.0;
	double deltaCurrent,deltaNext,deltaPrevious,maxOverlap;
	int prevIn=0,nextIn=0,prevOut=0,nextOut=0;
	boolean flag;
	int cpt=0;
	Double currentKey,nextKey,previousKey;
	EventLink currentEvent,nextEvent,previousEvent;
	double separation,defautSeparation=0.0;
	int catSuivant,catPrecedent;
	double minSep;
	double deltaT;
	int orderConflict=0;
	int indexIn=0,indexOut=0,deltaPosition=0;
	
	currentKey=new Double(eventIn.tIn);
	currentEvent=tableLinkEventIn.get(currentKey);
	if (currentEvent==null) System.out.println("Key not in Treemap Node for remove $$$$$$$$$$$$$$$$$$$$$$$$$ " +" flight="+ eventOut.flightNumber+" tIn="+eventOut.tIn+" link="+linkNumber);
	
	previousKey=tableLinkEventIn.lowerKey(currentKey);
	if (previousKey!=null)	prevIn=tableLinkEventIn.get(previousKey).decisionNumber;   
	nextKey=tableLinkEventIn.higherKey(currentKey);
	if (nextKey!=null) nextIn=tableLinkEventIn.get(nextKey).decisionNumber;


	currentKey=new Double(eventOut.tOut);
	currentEvent=tableLinkEventOut.get(currentKey);
	if (currentEvent==null) System.out.println("Key not in Treemap Node for remove $$$$$$$$$$$$$$$$$$$$$$$$$ " +" flight="+ eventOut.flightNumber+" tIn="+eventOut.tOut+" link="+linkNumber);
	
	previousKey=tableLinkEventOut.lowerKey(currentKey);
	if (previousKey!=null) prevOut=tableLinkEventOut.get(previousKey).decisionNumber;   
	nextKey=tableLinkEventOut.higherKey(currentKey);
	if (nextKey!=null) nextOut=tableLinkEventOut.get(nextKey).decisionNumber;
	
	if (prevIn!= prevOut) orderConflict++;
	if (nextIn!= nextOut) orderConflict++;
	return orderConflict;
	
    }


    




    
    public double evaluateLink(OperationsPerfo operationsPerfo){
	double separation,defautSeparation=0.0;
	int catSuivant,catPrecedent;
	double minSep;
	double deltaT;
	int nbConflitsRattrapage=0;
	EventLink eventIn,eventOut,previousEvent,nextEvent;

	int indexIn=0,indexOut=0,deltaPosition=0;
	double result;

	// Conflict checking at the link entry
	if (tableLinkEventIn.size()>=2){
	    previousEvent=tableLinkEventIn.get(tableLinkEventIn.firstKey());

	    for (Double nextKey : tableLinkEventIn.keySet())
		{
		    if (nextKey!=tableLinkEventIn.firstKey()){
			nextEvent=tableLinkEventIn.get(nextKey);
			
			
			catPrecedent=previousEvent.cat;
			catSuivant=nextEvent.cat;
			deltaT=nextEvent.tIn-previousEvent.tIn;
			
			separation=previousEvent.speed*deltaT;
			minSep=Constantes.TABLE_SEPARATION[catPrecedent][catSuivant];
			if ((separation)<minSep)
			    {
				//conflit de rattrapage
				
				defautSeparation=defautSeparation+(minSep-separation); 
				nbConflitsRattrapage=nbConflitsRattrapage+1;

				operationsPerfo.airspacePerfoSTAR[previousEvent.decisionNumber]++;
				operationsPerfo.airspacePerfoSTAR[nextEvent.decisionNumber]++;

				operationsPerfo.timeSTAR[previousEvent.decisionNumber]+=minSep-separation;
				operationsPerfo.timeSTAR[nextEvent.decisionNumber]+=minSep-separation;
        
				operationsPerfo.tableVoisins[previousEvent.decisionNumber][nextEvent.decisionNumber]+= minSep-separation;
				operationsPerfo.tableVoisins[nextEvent.decisionNumber][previousEvent.decisionNumber]+= minSep-separation;
	
			    }
			previousEvent=nextEvent;
		    }
		}
	}

	// Conflict checking at the link exit
	if (tableLinkEventOut.size()>=2){
	    previousEvent=tableLinkEventOut.get(tableLinkEventOut.firstKey());
	    for (Double nextKey : tableLinkEventOut.keySet())
		{
		    if (nextKey!=tableLinkEventOut.firstKey()){
			nextEvent=tableLinkEventOut.get(nextKey);

		
			
			catPrecedent=previousEvent.cat;
			catSuivant=nextEvent.cat;
			deltaT=nextEvent.tOut-previousEvent.tOut;

			separation=nextEvent.speed*deltaT;
			minSep=Constantes.TABLE_SEPARATION[catPrecedent][catSuivant];
			
			if ((separation)<minSep)
			    {
				//conflit de rattrapage
				defautSeparation=defautSeparation+(minSep-separation); 
				nbConflitsRattrapage=nbConflitsRattrapage+1;

				operationsPerfo.airspacePerfoSTAR[previousEvent.decisionNumber]++;
				operationsPerfo.airspacePerfoSTAR[nextEvent.decisionNumber]++;

				operationsPerfo.timeSTAR[previousEvent.decisionNumber]+=minSep-separation;
				operationsPerfo.timeSTAR[nextEvent.decisionNumber]+=minSep-separation;

				operationsPerfo.tableVoisins[previousEvent.decisionNumber][nextEvent.decisionNumber] += minSep-separation;
				operationsPerfo.tableVoisins[nextEvent.decisionNumber][previousEvent.decisionNumber] += minSep-separation;
			    }
			previousEvent=nextEvent;
		    }
		}
	}


	// Cheking the order of sequences
	for (Double nextKeyIn : tableLinkEventIn.keySet())
	    {
		eventIn=tableLinkEventIn.get(nextKeyIn);
		indexOut=0;
		for (Double nextKeyOut : tableLinkEventOut.keySet())
		    {
			eventOut=tableLinkEventOut.get(nextKeyOut);
			if (eventOut.decisionNumber==eventIn.decisionNumber){
			    deltaPosition=deltaPosition+Math.abs(indexOut-indexIn);
			    break;
			}
			indexOut++;
		    }
		indexIn++;
	    }

	nbConflitsRattrapage=nbConflitsRattrapage+deltaPosition*deltaPosition;
	defautSeparation=defautSeparation/1852;//defautSeparation expressed in NM
	
	result=nbConflitsRattrapage+defautSeparation;
	//result = nbConflitsRattrapage;
	return result;
    }
    
      
     
}
