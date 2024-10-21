package OPERATIONS.AIRSPACE;

public class EventLink {
    public int flightNumber;
    public int decisionNumber;
    public int cat;
    public double tIn;
    public double tOut;
    public double speed;
    public int nbConflictsLink;

    public EventLink(int flightNumber, int decisionNumber, int cat,double tIn, double tOut, double speed){
	this.flightNumber=flightNumber;
        this.decisionNumber=decisionNumber;
	this.cat=cat;
	this.tIn= tIn;
	this.tOut= tOut;
	this.speed= speed;
    }

    public String toString() {

	return (" { num Avion = " + flightNumber + " cat = " + cat + " tIn= " + tIn + " tOut= " + tOut + " speed= "+ speed + " nbConflictsLink= " + nbConflictsLink+ "}");

    }

}
