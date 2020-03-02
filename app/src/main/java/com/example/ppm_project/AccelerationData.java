package com.example.ppm_project;
import java.util.ArrayList;
//Using ArrayList, this is an array of non-fixed length.
//Data read from the csv file will need to be added to an ArrayList
//This can be done by iterating through each line, parsing the correct
//part as a double, then using arrayList.add(i).


public class AccelerationData
{
    private ArrayList<Double> sArray = new ArrayList<>();
    private ArrayList<Double> xArray = new ArrayList<>();
    private ArrayList<Double> yArray = new ArrayList<>();
    private ArrayList<Double> zArray = new ArrayList<>();

    public void setVals(ArrayList<Double> s, ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> z){
        sArray = s;
        xArray = x;
        yArray = y;
        zArray = z;
    }

    public boolean isPatientHavingEpisode() {


        Calibration calTest = new Calibration();

        double thresholdValue = calTest.calculateThreshold(sArray, calTest.getVarArray(sArray, calTest.calculateMagnitude(xArray, yArray, zArray)));
        double[] totalResults = calTest.getVarArray(sArray, calTest.calculateMagnitude(xArray, yArray, zArray));

        for (int i = 0; i < totalResults.length; i++) {
            if (totalResults[i] > thresholdValue) {
                return true;
            }
        }
        return false;
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
        ArrayList<Double> squaredDifferenceArray = new ArrayList<>();
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