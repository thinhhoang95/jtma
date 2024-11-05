package RECUIT;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class HyperReader {
    public HyperParameters readHyperparameters(String filename) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(filename)) {
            properties.load(input);
            String nbTransitions = properties.getProperty("nbTransitions");
            String alpha = properties.getProperty("alpha");
            String heatUntil = properties.getProperty("heatUntil");
            String tCutOffCoeff = properties.getProperty("tCutOffCoeff");
            System.out.println("Reading hyperparameters from: " + filename);
            System.out.println("nbTransitions: " + nbTransitions);
            System.out.println("alpha: " + alpha);
            System.out.println("heatUntil: " + heatUntil);
            System.out.println("tCutOffCoeff: " + tCutOffCoeff);
            HyperParameters hyperParameters = new HyperParameters();
            hyperParameters.setNbTransitions(Integer.parseInt(nbTransitions));
            hyperParameters.setAlpha(Double.parseDouble(alpha));
            hyperParameters.setHeatUntil(Double.parseDouble(heatUntil));
            hyperParameters.setTCutOffCoeff(Double.parseDouble(tCutOffCoeff));
            return hyperParameters;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}