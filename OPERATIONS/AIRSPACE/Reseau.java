package OPERATIONS.AIRSPACE;
import java.io.*;
import java.util.*;
import java.lang.*;
import OPERATIONS.*;
import OPERATIONS.FLIGHT.*;
/*
 * Reading all the nodes, links and routes
 */
public class Reseau {
    public static int nbNodesSTAR;
    public static int nbLinksSTAR;
    public static int nbRoutesSTAR;
    public static int nbEntrySTAR;
    public static Node[] tableNodeSTAR;
    public static Link[] tableLinkSTAR;
    public static RouteSet[] tableRouteSetSTAR; 
   
    public static void lireReseau(String nomGen){
	lireReseauSTAR(nomGen);
    }

        public static void readReseau(String nomGen){
	lireReseauSTAR(nomGen);
    }
    
    private static void lireReseauSTAR(String nomGen)
    {
	String nomFicNode =  nomGen + ".nodes";
	String nomFicLink =  nomGen + ".links";
	String nomFicRoute = nomGen + ".routes";
	String ligne;
	int isRwy;
	int i,ori,dest,link;
	double x,y,dist,dx,dy;
	int type=0, first_angle=0,second_angle=0;
	double pourcentage=0.0;
        int timeStartBlocked,timeEndBlocked;
	
	String buffer="";
	
	try {
	    FileReader fileNode = new FileReader(nomFicNode);
	    BufferedReader fileInputNode = new BufferedReader(fileNode);

	    FileReader fileLink = new FileReader(nomFicLink);
	    BufferedReader fileInputLink = new BufferedReader(fileLink);

	    FileReader fileRoute = new FileReader(nomFicRoute);
	    BufferedReader fileInputRoute = new BufferedReader(fileRoute);


	    /*************************************************************************/
	    /*************************************************************************/
	    fileInputNode.mark(1000000);
	    System.out.println("STAR Nodes reading ...");
	    nbNodesSTAR=0;
	    //lecture de la ligne de commentaire
	    // ligne = fileInputNode.readLine();
	    while ((ligne = fileInputNode.readLine()) != null) 
		{
		    nbNodesSTAR++;
		    StringTokenizer tokenizer = new StringTokenizer(ligne);
		}
	    tableNodeSTAR=new Node[nbNodesSTAR];
	    fileInputNode.reset();
	     //lecture de la ligne de commentaire
	    // ligne = fileInputNode.readLine();
	    i=0;
	    while  ((ligne = fileInputNode.readLine()) != null) 
		{
		    StringTokenizer tokenizer = new StringTokenizer(ligne);
		    if (tokenizer.hasMoreTokens()) {
			//position of node
			// buffer=tokenizer.nextToken();
			x=Constantes.NM_TO_METER*Double.parseDouble(tokenizer.nextToken());
			y=Constantes.NM_TO_METER*Double.parseDouble(tokenizer.nextToken());
			isRwy=Integer.parseInt(tokenizer.nextToken());
			tableNodeSTAR[i]=new Node(x,y,i,isRwy);
			i++;
		    }
		}


	    /*************************************************************************/
	    /*************************************************************************/
	    /* Link file reading  */
	    System.out.println("STAR Links reading ...");
	    fileInputLink.mark(1000000);
	    nbLinksSTAR=0;
	     //lecture de la ligne de commentaire
	    // ligne = fileInputLink.readLine();
	    while ((ligne = fileInputLink.readLine()) != null) 
		{
		    nbLinksSTAR++;
		}
	    tableLinkSTAR=new Link[nbLinksSTAR];
	    //lecture de la ligne de commentaire
	    
	    fileInputLink.reset();
	    //lecture de la ligne de commentaire
	    // ligne = fileInputLink.readLine();
	    i=0;
	    while  ((ligne = fileInputLink.readLine()) != null) 
		{
		    StringTokenizer tokenizer = new StringTokenizer(ligne);
		    // buffer=tokenizer.nextToken();
                    // origin
		    ori=Integer.parseInt(tokenizer.nextToken())-1;
                    // destination
		    dest=Integer.parseInt(tokenizer.nextToken())-1;
		    
		    type=Integer.parseInt(tokenizer.nextToken());

		    if (type==1) first_angle=Integer.parseInt(tokenizer.nextToken());
		    if (type==2) {
			first_angle=Integer.parseInt(tokenizer.nextToken());
			second_angle=Integer.parseInt(tokenizer.nextToken());
		    }
		    if (type==3) pourcentage=Double.parseDouble(tokenizer.nextToken());
		    //length computation
		   
		    dx=tableNodeSTAR[dest].x-tableNodeSTAR[ori].x;
		    dy=tableNodeSTAR[dest].y-tableNodeSTAR[ori].y;
		    dist=Math.sqrt(dx*dx+dy*dy);
		    tableLinkSTAR[i]=new Link(ori,dest,type,first_angle,second_angle,pourcentage,dist,i);
		    i++;
		}

	    System.out.println("STAR NbLink*********"+nbLinksSTAR);

	    /*************************************************************************/
	    /*************************************************************************/
	    fileInputRoute.mark(1000000);
	    System.out.println("STAR Routes reading ...");
	    nbEntrySTAR=0;
	    while ((ligne = fileInputRoute.readLine()) != null) 
		{
		    nbEntrySTAR++;
		    StringTokenizer tokenizer = new StringTokenizer(ligne);
		}

	    tableRouteSetSTAR=new RouteSet[nbEntrySTAR];
	    fileInputRoute.reset();
	    i=0;
	    while  ((ligne = fileInputRoute.readLine()) != null) 
		{
		    String[] listString = ligne.split(";");
		    
		    tableRouteSetSTAR[i]=new RouteSet(listString.length);
		    tableRouteSetSTAR[i].entry=i;
		    for (int j=0;j<listString.length;j++)
			{
			    String[] listSubString = listString[j].split(" ");
			    int[] tab=new int[listSubString.length];
			    for (int k=0;k<listSubString.length;k++)
				{
				    tab[k]=Integer.parseInt(listSubString[k])-1;
				}
			    tableRouteSetSTAR[i].tableRoute[j]=new Route(tab);
			}
		    tableRouteSetSTAR[i].computeRouteExtension();
		    i++;
		}
	    
	    fileInputNode.close();
	    fileInputLink.close();
	    fileInputRoute.close();

	} catch(IOException ioe) {
	    System.err.println("File reading error");
	}
    }    
 
  
    
    /*********************Reset links nodes*************************************************/
    public static void resetLinksNodes()
    {

	for (int i=0;i<tableNodeSTAR.length;i++)
	    {
		tableNodeSTAR[i].clearTableEvent();
	    }

	for (int i=0;i<tableLinkSTAR.length;i++)
	    {
		tableLinkSTAR[i].clearTableLinkEvent();
	    }	
    }



    public static void evaluateAllLink(){
	int dimension=FlightSet.nbFlights;
	OperationsPerfo operationsPerfo=new OperationsPerfo(dimension);
	double res;
	
	for (int i=0;i<nbLinksSTAR;i++){
	    
	    res=tableLinkSTAR[i].evaluateLink(operationsPerfo);
	    
	}
    }

   public static void evaluateAllNode(){
	int dimension=FlightSet.nbFlights;
	OperationsPerfo operationsPerfo=new OperationsPerfo(dimension);
	double res;
	
	for (int i=0;i<nbNodesSTAR;i++){
	    res=tableNodeSTAR[i].evaluateNode(operationsPerfo);
	    
	}
    }

    
    public static void main(String[] args) {
	Reseau monReseau = new Reseau();
	monReseau.lireReseau("DATA/SHEN_ZHEN/ZGSZ_PMS");
	
	System.out.println("STAR*******************" + tableRouteSetSTAR.length);
	for (int i=0;i<tableRouteSetSTAR.length;i++){
	    tableRouteSetSTAR[i].afficherRouteSet();
	}

    }







}
