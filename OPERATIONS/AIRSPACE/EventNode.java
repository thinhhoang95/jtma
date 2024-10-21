package OPERATIONS.AIRSPACE;

public class EventNode {
    public int flightNumber;
    public int decisionNumber;
    public double tIn;
    public double tOut;
    public int nbConflictsNode;

    public EventNode(int flightNumber,int decisionNumber,double tIn,double tOut){
	this.flightNumber=flightNumber;
        this.decisionNumber=decisionNumber;
	this.tIn=tIn;
	this.tOut=tOut;
    }

    public String toString() {

	return (" { num Avion = " + flightNumber + " Decision number "+decisionNumber+ "tIn = " + tIn + " tOut = " + tOut+ "nbConflictsNode= "+ nbConflictsNode+ "}");

    }
}
