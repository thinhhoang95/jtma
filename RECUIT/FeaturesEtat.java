package RECUIT;
public class FeaturesEtat{

    public double objective;
    public double numConflicts=0.0;
    public double evalNodes=0.0;
    public double evalLinks=0.0;
    public double evalDelay=0.0;
    public double evalSpeed=0.0;
    public double evalRoute=0.0;

    
    public static void copy(FeaturesEtat in, FeaturesEtat out) {
	out.objective=in.objective;
	out.numConflicts=in.numConflicts;
	out.evalNodes=in.evalNodes;
	out.evalLinks=in.evalLinks;
	out.evalDelay=in.evalDelay;
	out.evalSpeed=in.evalSpeed;
	out.evalRoute= in.evalRoute;
    }

    
    public String afficherFeaturesEtat() {
	String buffer = "";
	buffer = buffer + " Objective = " + String.format("%.2f", objective) + " ";
	buffer = buffer + "Num Conflicts = " + (int)numConflicts + " ";
	buffer = buffer + "EvalNodes = " + String.format("%.2f", evalNodes) + " ";
	buffer = buffer + "EvalLinks = " + String.format("%.2f", evalLinks) + " ";
	buffer = buffer + "EvalDelay = " + String.format("%.2f", evalDelay) + " ";
	buffer = buffer + "EvalSpeed = " + String.format("%.2f", evalSpeed) + " ";
	buffer = buffer + "EvalRoute = " + String.format("%.2f", evalRoute) + " ";
	return buffer;
    }

	public String afficherFeaturesEtatConflitsSeuls() {
		String buffer = "";
		buffer = buffer + "CF: " + (int)numConflicts + " ";
		return buffer;
	}

    public String saveFeaturesEtat() {
	String buffer = "";
	buffer = buffer + objective + " ";
	buffer = buffer + numConflicts + " ";
	buffer = buffer + evalNodes + " ";
	buffer = buffer + evalLinks + " ";
	buffer = buffer + evalDelay + " ";
	buffer = buffer + evalSpeed + " ";
	buffer = buffer + evalRoute + " \n";
	return buffer;
    }
    

    public void razFeaturesEtat(){
	objective = 0.0;
	numConflicts = 0.0;
	evalNodes = 0.0;
	evalLinks = 0.0;
	evalDelay = 0.0;
	evalSpeed = 0.0;
	evalRoute=0.0;
    }
    


}
