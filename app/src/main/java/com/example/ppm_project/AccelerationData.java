package com.example.ppm_project;
import java.util.ArrayList;
//Using ArrayList, this is an array of non-fixed length.
//Data read from the csv file will need to be added to an ArrayList
//This can be done by iterating through each line, parsing the correct
//part as a double, then using arrayList.add(i).

public class AccelerationData
{

    private ArrayList<Double> xArray = new ArrayList<>();
    private ArrayList<Double> yArray = new ArrayList<>();
    private ArrayList<Double> zArray = new ArrayList<>();

    public void setVals(ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> z){
        xArray = x;
        yArray = y;
        zArray = z;
    }

    public boolean isPatientHavingEpisode()
    {
        //System.out.println("X Variance: " + calcVariance(xArray));
        //System.out.println("Y Variance: " + calcVariance(yArray));
        //System.out.println("Z Variance: " + calcVariance(zArray));

        // Must now compare against a 'threshold' value.
        // The variance of the data gathered was:
        // x (NORMAL): 0.0315 (ERRATIC): 0.383
        // y (NORMAL): 0.127  (ERRATIC): 0.35
        // z (NORMAL): 0.042  (ERRATIC): 0.213

        // Perhaps somewhere around 0.2 as a minimum threshold for erratic movement?

        //0.2 will need to be replaced when calculateThreshold has been implemented
        if (calcVariance(calculateMagnitude(xArray, yArray, zArray), 0, calculateMagnitude(xArray, yArray, zArray).size()) > 0.2){
            return true;
        } else {
            return false;
        }
    }
    // Calculates √(x² + y² + z²) so that we're working with a single array.
    // In theory, this is the magnitude of the vector, showing acceleration in all directions.
    public ArrayList<Double> calculateMagnitude(ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> z) {
        ArrayList<Double> magnitudeOfAccel = new ArrayList<>();

        for (int i = 0; i < x.size() - 1; i++) {
            double toBeInserted = Math.sqrt((x.get(i)*x.get(i)) + (y.get(i)*y.get(i)) + (z.get(i)*z.get(i)));
            magnitudeOfAccel.add(toBeInserted);
        }

        return(magnitudeOfAccel);
    }


    // Calculates the mean of an array by iterating through.
    // Will be needed to calculate variance.
    public double calcArrayMean(ArrayList<Double> givenArray, int startValue, int endValue) {
        double total = 0;

        for (int i = 0; i < givenArray.size(); i++) {
            total = total + givenArray.get(i);
        }

        double arrayMean = total / givenArray.size();
        return (arrayMean);
    }

    // Calculates the variance of an array.
    public double calcVariance(ArrayList<Double> givenArray, int startValue, int endValue) {
        ArrayList<Double> squaredDifferenceArray = new ArrayList<Double>();
        double sqDifference;
        double variance;
        double currentValue;

        //Calculate the mean of the array first
        double meanOfArray = calcArrayMean(givenArray, startValue, endValue);

        //Subtract the mean from each value in the array
        //Square the result
        for (int i = 0 + startValue; i < endValue; i++ ) {
            currentValue = (givenArray.get(i) - meanOfArray);
            sqDifference = currentValue * currentValue;
            squaredDifferenceArray.add(sqDifference);
        }

        //Average of those squared differences = variance
        variance = calcArrayMean(squaredDifferenceArray, startValue, endValue);

        return (variance);
    }

}