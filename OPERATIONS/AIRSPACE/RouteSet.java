package OPERATIONS.AIRSPACE;
public class RouteSet {
    public int entry;
    public int size;
    public Route[] tableRoute;

    public RouteSet(int size){
	this.size=size;
	tableRoute=new Route[size];
    }

    public void addRoute(int i, Route uneRoute){
	tableRoute[i]=new Route(uneRoute);
    }

    public int indexLongestRoute(){

	double maxLength=-1000000;
	int index=0;
	
	for (int i=0;i<size;i++){
	    if (tableRoute[i].routeLength>maxLength){
		maxLength=tableRoute[i].routeLength;
		index=i;
	    }
	}
	return index;
    }

    public int indexShortestRoute(){

	double minLength=1000000;
	int index=0;
	
	for (int i=0;i<size;i++){
	    if (tableRoute[i].routeLength<minLength){
		minLength=tableRoute[i].routeLength;
		index=i;
	    }
	}
	return index;
    }

    public void computeRouteExtension() {
	int indexShortest=indexShortestRoute();
	double shortestRouteLength=tableRoute[indexShortest].routeLength;
	for (int i=0;i<size;i++){
	    tableRoute[i].routeExtension=(tableRoute[i].routeLength-shortestRouteLength)/shortestRouteLength;
	}
    }
    
    
    public void afficherRouteSet(){

	for (int i=0;i<size;i++){
	    System.out.println("Entry="+entry);
	    tableRoute[i].afficherRoute();
	}
    }

}
