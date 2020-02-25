package com.example.ppm_project;
import java.util.Arrays;
import java.util.ArrayList;

public class Calibration {

    public double calculateThreshold(ArrayList<Double> s, ArrayList<Double> magnitudeOfAccel) {

        getVarArray(s, magnitudeOfAccel);

        return(0.0);
    }

    //Calculates the variance using the calcVariance function found in AccelerationData.java
    //Variance is calculated every ten seconds, and then added to a variance array.
    //We will take the 95th percentile from this array.
    public double[] getVarArray (ArrayList<Double> s, ArrayList<Double> magnitudeOfAccel) {
        int lastStop = 0;
        int nextStop = 10;
        ArrayList<Double> variances = new ArrayList<>();
        AccelerationData varCalculator = new AccelerationData ();

        for (int i = 0; i < s.size() - 1; i++) {
            //try {
            //Thread.sleep(1000);
            //^ Will be used to trickle the data slowly. Not currently useful for testing.
            if (s.get(i) == 0) {
                variances.add(varCalculator.calcVariance(magnitudeOfAccel, lastStop, nextStop));
            }

            if (s.get(i) == nextStop) {
                nextStop = nextStop + 10;
                lastStop = lastStop + 10;

                if (nextStop < magnitudeOfAccel.size()) {
                    variances.add(varCalculator.calcVariance(magnitudeOfAccel, lastStop, nextStop));
                }
            }

            //} catch(InterruptedException e) { System.out.println("Caught an error somewhere, maybe");}
            //  Necessary for Thread.sleep when implemented.

        }

        System.out.println(variances);

        return(sortVarArray(variances));
    }

    // Sorts the array from the getVarArray function in ascending order.
    public double[] sortVarArray (ArrayList<Double> varArray) {
        double[] sortedVarArray = new double[varArray.size()];

        for (int i = 0; i < varArray.size(); i++) {
            sortedVarArray[i] = varArray.get(i);
        }

        Arrays.sort(sortedVarArray);
        System.out.println(Arrays.toString(sortedVarArray));

        return (sortedVarArray);
    }
}
