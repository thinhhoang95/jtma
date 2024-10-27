package RECUIT;

public class HyperParameters {
    private int nbTransitions;
    private double alpha;
    private double heatUntil;

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
}
