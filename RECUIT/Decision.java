package RECUIT;

import OPERATIONS.AIRSPACE.Reseau;
import OPERATIONS.FLIGHT.*;
import OPERATIONS.*;

import java.util.*;

public class Decision{
 
    public static Random generateur=new Random(123);

    public int flightNumber;//initial order
    public int entryNumber;
    
    public int decisionNumber;//sorted order

    /*Flight decisions*/
    public int nbSlotsRTA;
    public int routeNumber;
    public int nbPourcentSpeed;
    public double firstPourcentInMergeLeg;
    public double secondPourcentInMergeLeg;
    public boolean leftRight;
    public double pourcentInShortCut;
    
  
    /* A posteriori performance of the flight*/
    public double y;
    public PerfoDecision perfoDecision;
    public double probaMutation;
    
    
    /*************************Constructor***********************************************/
    public Decision(){
	perfoDecision=new PerfoDecision();
    }

    public Decision(int flightNumber){
	this.flightNumber=flightNumber;
	entryNumber=FlightSet.tableFlight[flightNumber].entryNumber;;
	decisionNumber=flightNumber;
	perfoDecision=new PerfoDecision();
    }
    
    
    public static void copy(Decision in, Decision out){
	out.flightNumber=in.flightNumber;
	out.decisionNumber=in.decisionNumber;
	out.nbSlotsRTA=in.nbSlotsRTA;
	out.routeNumber=in.routeNumber;
	out.nbPourcentSpeed=in.nbPourcentSpeed;
	out.firstPourcentInMergeLeg=in.firstPourcentInMergeLeg;
	out.secondPourcentInMergeLeg=in.secondPourcentInMergeLeg;
	out.leftRight=in.leftRight;
	out.pourcentInShortCut=in.pourcentInShortCut;
	PerfoDecision.copy(in.perfoDecision,out.perfoDecision);
    }


    
    /********************************afficher le résultat********************************/

    public String afficherDecision(){
	String res = "";
	res = res + "DecisionNumber= " + decisionNumber; 
	res = res + " NbSlotsRTA= " + nbSlotsRTA + " RouteNumber= " + routeNumber + " NbPourcentSpeed= " + nbPourcentSpeed + "+FirstPourcentInMergeLeg="+ firstPourcentInMergeLeg;
	res = res + " SecondPourcentInMergeLeg= " + secondPourcentInMergeLeg+ " LeftRight= " + leftRight + " PourcentInShortCut= " + pourcentInShortCut;
	res = res + " Perfo Node = " + perfoDecision.evalNodes + " Perfo Link = " + perfoDecision.evalLinks;
	return res;
    }
    

    
    

    /*********************************Initialiser *************************************/

	  
    public void initDecision() {
	    nbSlotsRTA=0;
	    routeNumber=0;
	    nbPourcentSpeed=0;
	    firstPourcentInMergeLeg=0.0;
	    secondPourcentInMergeLeg=0.0;
	    leftRight=false;
	    pourcentInShortCut=0.0;
    }
    
					   
    public void initAleatDecision() {
	if (Constantes.SLOT_RTA_CHANGE)
	    slotRTAChange();
	if (Constantes.ROUTE_CHANGE)
	    routeChange();
	if (Constantes.SPEED_CHANGE)
	    speedChange();
	changeFirstMergeDecision();
	changeSecondMergeDecision();
	changeLeftRight();
	changePourcentInShortCut();
    }


    
	

    /*********************************Changer Decision *************************************/

    private void slotRTAChange() {
	if (generateur.nextDouble()<0.5){
	    //late entering
	    if (generateur.nextBoolean()){
		nbSlotsRTA=generateur.nextInt(Constantes.DELTA_T_RTA_PLUS+1);
	    } else {
		nbSlotsRTA=nbSlotsRTA+1;
		if (nbSlotsRTA>Constantes.DELTA_T_RTA_PLUS) nbSlotsRTA=Constantes.DELTA_T_RTA_PLUS;
	    }
	}
	else{
	    //early entering
	    if (generateur.nextBoolean()){
		nbSlotsRTA=-generateur.nextInt(Constantes.DELTA_T_RTA_MOINS+1);
	    } else {
		nbSlotsRTA=nbSlotsRTA-1;
		if (nbSlotsRTA<-Constantes.DELTA_T_RTA_MOINS) nbSlotsRTA=-Constantes.DELTA_T_RTA_MOINS;
	    }
	}
    }

    private void routeChange() {
	 int entryNumber= FlightSet.tableFlight[flightNumber].entryNumber;
	 int numberOfRouteChoice=Reseau.tableRouteSetSTAR[entryNumber].size;
	 
	 routeNumber=generateur.nextInt(numberOfRouteChoice);
	 
    }

    private void speedChange() {
	if (generateur.nextBoolean()){
	    //speedUp
	    if (generateur.nextBoolean()){
		nbPourcentSpeed=generateur.nextInt(Constantes.DELTA_SPEED_PLUS+1);
	    }else {
		nbPourcentSpeed=nbPourcentSpeed+1;
		if (nbPourcentSpeed>Constantes.DELTA_SPEED_PLUS) nbPourcentSpeed=Constantes.DELTA_SPEED_PLUS;
	    }
	}
	else{
	    //slow down
	    if (generateur.nextBoolean()){
		nbPourcentSpeed=-generateur.nextInt(Constantes.DELTA_SPEED_MOINS+1);
	    } else {
		nbPourcentSpeed=nbPourcentSpeed-1;
		if (nbPourcentSpeed<-Constantes.DELTA_SPEED_MOINS) nbPourcentSpeed=-Constantes.DELTA_SPEED_MOINS;
	    }
	}
    }    



    

    public void changeFirstMergeDecision(){
	firstPourcentInMergeLeg=generateur.nextDouble();
    }

	public void changeFirstMergeDecisionTo(float pourcent){
		firstPourcentInMergeLeg = pourcent;
	}
    
    public void changeSecondMergeDecision(){
	secondPourcentInMergeLeg=generateur.nextDouble();
    }


    public void changeLeftRight(){
	leftRight=generateur.nextBoolean();
    }


    public void changePourcentInShortCut(){
	pourcentInShortCut=generateur.nextDouble();
    }


    
    public void changeDecision(double ratioTemperature){
	double tirage=generateur.nextDouble();
      
	if (Constantes.ROUTE_CHANGE) {
	    if (tirage<0.33) routeChange();
	}
	if(Constantes.SLOT_RTA_CHANGE) {
	    // if ((tirage>0.3)&&(tirage<0.7)) slotRTAChange();
		slotRTAChange(); // always change the slotRTA
	}
	if(Constantes.SPEED_CHANGE) {
	    if ((tirage>0.6)&&(tirage<0.8)) speedChange();
	}
    // TODO: ENABLE THIS FOR PMS CASE: entryNumber 0,1,2,3 have a PMS link
	// Pull PMS decision from GlobalSettings
	
	int[] entryWithPMS = GlobalSettings.getEntryWithPMS();
	if (Arrays.stream(entryWithPMS).anyMatch(x -> x == entryNumber)) {
		changeFirstMergeDecision();
	}

	// if (entryNumber < 4)
	// {
	// 	if (flightNumber == 1)
	// 		changeFirstMergeDecisionTo(0.5f); // Use PMS if flight number is 1
	// 	else
	// 		changeFirstMergeDecisionTo(0.0f); // Don't use PMS if flight number is not 1
	// }

	
	// if (entryNumber>=2) changeFirstMergeDecision();

	// if (entryNumber==7) changeSecondMergeDecision();

	// if ((entryNumber==2)||(entryNumber==6)||(entryNumber==7)||(entryNumber==6)) changePourcentInShortCut();
    }                                                                 //typ0?????

 

    
    public void oldchangeDecision(double ratioTemperature){
	double tirage=generateur.nextDouble();
        if (tirage<ratioTemperature+0.1) routeChange();
	else if (tirage<0.5) slotRTAChange();
	else speedChange();
	 changeFirstMergeDecision();
    }

    
}
