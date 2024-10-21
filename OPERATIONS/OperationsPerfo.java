package OPERATIONS;
import java.util.*;

public class OperationsPerfo {

    public int dimension;
    public int[] airspacePerfoSTAR;

    public double[] timeSTAR;
    public double[] timeRwyIn;
    
    public double[][] tableVoisins;


    public OperationsPerfo(int dimension) {
	this.dimension=dimension;
	airspacePerfoSTAR=new int[dimension];	
	
	timeSTAR=new double[dimension];
	timeRwyIn=new double[dimension];
    
	tableVoisins=new double[dimension][dimension];
    }

    public void resetOperationsPerfo(){
	for (int i=0;i<dimension;i++){
	    airspacePerfoSTAR[i]=0;
	    timeSTAR[i]=0.0;
	    timeRwyIn[i]=0.0;
	}
	
	for (int i=0;i<dimension;i++){
	    for (int j=0;j<dimension;j++){
		tableVoisins[i][j]=0.0;	
	    }
	}	
    }


    public ArrayList<Integer> chercherVoisins(int indice){
	ArrayList<Integer> liste =new ArrayList<Integer>();
	
        for (int j=0;j<dimension;j++){
	    if (tableVoisins[indice][j]!=0){
		liste.add(j);
	    }
	}
	return liste;
    }

}
