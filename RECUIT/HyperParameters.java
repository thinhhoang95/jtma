package RECUIT;

public class HyperParameters {
    private int nbTransitions;
    private double alpha;
    private double heatUntil;
    private double tCutOffCoeff;

    public int getNbTransitions() {
        return nbTransitions;
    }

    public void setNbTransitions(int nbTransitions) {
        this.nbTransitions = nbTransitions;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getHeatUntil() {
        return heatUntil;
    }

    public void setHeatUntil(double heatUntil) {
        this.heatUntil = heatUntil;
    }

    public double getTCutOffCoeff() {
        return tCutOffCoeff;
    }

    public void setTCutOffCoeff(double tCutOffCoeff) {
        this.tCutOffCoeff = tCutOffCoeff;
    }
}
