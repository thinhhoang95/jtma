package OPERATIONS.AIRSPACE;
import OPERATIONS.*;
import java.util.*;
public class Node {
    public double x;
    public double y;
    public int nodeNumber;
    public int isRwy;


    public TreeMap<Double,EventNode> tableNodeEvent=new TreeMap<Double,EventNode>();


    public Node(double x,double y, int nodeNumber,int isRwy) {
	this.x=x;
	this.y=y;
	this.nodeNumber=nodeNumber;
	this.isRwy=isRwy;
    }


    public void clearTableEvent(){
	tableNodeEvent.clear();
    }

    public void updateEventNode(EventNode unEvent,boolean put) {
	EventNode buffer;
	if (put) {

	    if (tableNodeEvent.containsKey(new Double(unEvent.tIn))){
		System.out.println("Same key on node $$$$$$$$$$$$$$$$$$$$$$$$$ " + unEvent.tIn + " " + unEvent.flightNumber); 
	    }
	    tableNodeEvent.put(new Double(unEvent.tIn),unEvent);

	}
	else {
	    buffer=tableNodeEvent.remove(new Double(unEvent.tIn));
	    if (buffer==null) System.out.println("Key not in Treemap Node for remove $$$$$$$$$$$$$$$$$$$$$$$$$ " +" flight="+ unEvent.flightNumber+" tIn="+unEvent.tIn+" node="+nodeNumber);

	}
    }


    public double evaluateEventNode(EventNode unEvent) {
	double result=0.0;
	double ecart,defaut=0.0;
	double deltaCurrent,deltaNext,deltaPrevious,maxOverlap;
	boolean flag;
	int cpt=0;
	Double currentKey,nextKey,previousKey;
	EventNode currentEvent,nextEvent,previousEvent;
	
	
	currentKey=new Double(unEvent.tIn);
	currentEvent=tableNodeEvent.get(currentKey);
	if (currentEvent==null) System.out.println("Key not in Treemap Node for remove $$$$$$$$$$$$$$$$$$$$$$$$$ " +" flight="+ unEvent.flightNumber+" tIn="+unEvent.tIn+" node="+nodeNumber);



	// logging les evenements dans le noeud
	RECUIT.GlobalSettings.writeToNodeLogFile("***");
	RECUIT.GlobalSettings.writeToNodeLogFile("node=" + nodeNumber);
	RECUIT.GlobalSettings.writeToNodeLogFile("Current flight:");
	RECUIT.GlobalSettings.writeToNodeLogFile(RECUIT.GlobalSettings.showFlightInfo(currentEvent.flightNumber));
	RECUIT.GlobalSettings.writeToNodeLogFile("tIn=" + currentEvent.tIn + " tOut=" + currentEvent.tOut);
	RECUIT.GlobalSettings.writeToNodeLogFile("***");



	//recherche des conflits en aval / downstream
	previousKey=currentKey;
	nextKey=tableNodeEvent.higherKey(currentKey);
	do {
	    flag=false;  
	    if (nextKey!=null){
		nextEvent=tableNodeEvent.get(nextKey);
		ecart=nextEvent.tIn-currentEvent.tOut;  //time overlap
		if (ecart<0.0)
		    {
			deltaCurrent=currentEvent.tOut-currentEvent.tIn;
			deltaNext=nextEvent.tOut-nextEvent.tIn;
			maxOverlap=deltaCurrent/2.0;
			if (deltaNext>deltaCurrent) maxOverlap=deltaNext/2.0; //???why not maxOverlap = Math.min(deltaCurrent, deltaNext) / 2.0;
			//conflit
			defaut=defaut-ecart/maxOverlap;   //accumulating a measure of conflict severity
		
			cpt++;
			flag=true;
		    }
		nextKey=tableNodeEvent.higherKey(nextKey);
	    }
	}while (flag);
	
	//recherche des conflits en amont / upstream
	nextKey=currentKey;
	previousKey=tableNodeEvent.lowerKey(currentKey);
	do {
	    flag=false;  
	    if (previousKey!=null){
		previousEvent=tableNodeEvent.get(previousKey);
		ecart=currentEvent.tIn-previousEvent.tOut;
		if (ecart<0.0)
		    {
			deltaCurrent=currentEvent.tOut-currentEvent.tIn;
			deltaPrevious=previousEvent.tOut-previousEvent.tIn;
			maxOverlap=deltaCurrent/2.0;
			if (deltaPrevious>deltaCurrent) maxOverlap=deltaPrevious/2.0;
			//conflit
			defaut=defaut-ecart/maxOverlap;
			cpt++;

			// Show the related flight info
			RECUIT.GlobalSettings.writeToConflictLogFile("***");
			RECUIT.GlobalSettings.writeToConflictLogFile("node=" + nodeNumber);
			RECUIT.GlobalSettings.writeToConflictLogFile("Previous flight:");
			RECUIT.GlobalSettings.writeToConflictLogFile(RECUIT.GlobalSettings.showFlightInfo(previousEvent.flightNumber));
			RECUIT.GlobalSettings.writeToConflictLogFile("tIn=" + previousEvent.tIn + " tOut=" + previousEvent.tOut);
			RECUIT.GlobalSettings.writeToConflictLogFile("---");
			RECUIT.GlobalSettings.writeToConflictLogFile("Current flight:");
			RECUIT.GlobalSettings.writeToConflictLogFile(RECUIT.GlobalSettings.showFlightInfo(currentEvent.flightNumber));
			RECUIT.GlobalSettings.writeToConflictLogFile("tIn=" + currentEvent.tIn + " tOut=" + currentEvent.tOut);
			
			RECUIT.GlobalSettings.writeToConflictLogFile("***");   
			flag=true;
		    }
		previousKey=tableNodeEvent.lowerKey(previousKey);
	    }
	}while (flag);
	result=defaut + cpt;
	unEvent.nbConflictsNode=cpt;
	return result;
	
    }


    

    public double evaluateNode(OperationsPerfo operationsPerfo){
	double result=0.0;
	double ecart,defaut=0.0;
	double deltaPrevious,deltaNext,maxOverlap;
	int cpt=0;
	
	if (tableNodeEvent.size()>=2){
	    EventNode previousEvent=tableNodeEvent.get(tableNodeEvent.firstKey());
	    EventNode nextEvent;
	    for (Double nextKey : tableNodeEvent.keySet())
		{
		    if (nextKey!=tableNodeEvent.firstKey()){
			nextEvent=tableNodeEvent.get(nextKey);

			

			if (nextEvent!=previousEvent) {
			    ecart=nextEvent.tIn-previousEvent.tOut;

			    if (ecart<0.0)
				{
				    deltaPrevious=previousEvent.tOut-previousEvent.tIn;
				    deltaNext=nextEvent.tOut-nextEvent.tIn;
				    maxOverlap=deltaPrevious/2.0;
				    if (deltaNext>deltaPrevious) maxOverlap=deltaNext/2.0;

				    //conflit
				    defaut=defaut-ecart/maxOverlap;
				   
				    cpt++;
			
				    operationsPerfo.airspacePerfoSTAR[previousEvent.decisionNumber]++;
				    operationsPerfo.airspacePerfoSTAR[nextEvent.decisionNumber]++;

				    operationsPerfo.timeSTAR[previousEvent.decisionNumber]+=-ecart;
				    operationsPerfo.timeSTAR[nextEvent.decisionNumber]+=-ecart;
	        
				    operationsPerfo.tableVoisins[previousEvent.decisionNumber][nextEvent.decisionNumber]+= -ecart;
				    operationsPerfo.tableVoisins[nextEvent.decisionNumber][previousEvent.decisionNumber]+= -ecart;			    
			    
				}
			}
			previousEvent=nextEvent;
		    }
		}
	}
	result=defaut + cpt;
	//result=cpt;
	return result;
    }

}
