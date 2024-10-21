package RECUIT;
public class PerfoDecision{

    public double objective;
    public double numConflicts=0.0;
    public double evalNodes=0.0;
    public double evalLinks=0.0;
    public double evalDelay=0.0;
    public double evalSpeed=0.0;
    public double evalRoute=0.0;

    
    public static void copy(PerfoDecision in, PerfoDecision out) {
	out.objective=in.objective;
	out.numConflicts=in.numConflicts;
	out.evalNodes=in.evalNodes;
	out.evalLinks=in.evalLinks;
	out.evalDelay=in.evalDelay;
	out.evalSpeed=in.evalSpeed;
	out.evalRoute=in.evalRoute;
    }

    
    public String afficherPerfoDecision() {
	String buffer = "";
	buffer = buffer + "Objective= " + objective + " ";
	buffer = buffer + "Num Conflicts= " + numConflicts + " ";
	buffer = buffer + "EvalNodes= " + evalNodes + " ";
	buffer = buffer + "EvalLinks= " + evalLinks + " ";
	buffer = buffer + "EvalDelay= " + evalDelay + " ";
	buffer = buffer + "EvalSpeed= " + evalSpeed + " ";
	buffer = buffer + "EvalRoute= " + evalRoute + " \n";
	return buffer;
    }

    public String savePerfoDecision() {
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
    

    public void razPerfoDecision(){
	objective = 0.0;
	numConflicts = 0.0;
	evalNodes = 0.0;
	evalLinks = 0.0;
	evalDelay = 0.0;
	evalSpeed = 0.0;
	evalRoute=0.0;
    }
    


}
