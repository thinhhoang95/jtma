package OPERATIONS.AIRSPACE;
public class Route {
    public int size;
    public int[] tableIndexLink;
    public double routeLength;
    public double routeExtension;

    public Route(int[] tab){
	this.size=tab.length;
	tableIndexLink=new int[tab.length];
	for (int i=0;i<tab.length;i++){
	    tableIndexLink[i]=tab[i];
	}

	routeLength=routeLength();
    }

    public Route(Route uneRoute){
        new Route(uneRoute.tableIndexLink);
    }

    public double routeLength(){
	double val=0.0;

	for (int i=0;i<size;i++){
	    
	    val=val+Reseau.tableLinkSTAR[tableIndexLink[i]].length;
	}

	return val;
    }

    
    public void  afficherRoute(){
	System.out.println("Route ");
	for (int i=0;i<size;i++)
	    {
		System.out.print(" " + (tableIndexLink[i]+1));
	    }
	System.out.println();
    }

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(" ").append(tableIndexLink[i] + 1);
		}
		return sb.toString();
	}

}
