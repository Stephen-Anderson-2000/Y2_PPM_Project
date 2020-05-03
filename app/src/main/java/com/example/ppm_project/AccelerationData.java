
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

    // Returns true only if the given data has three consecutive variance values
    // above the threshold value. This means the patient must have at least a 30
    // second interval of values with high variance. This prevents a wave for example
    // from triggering the detection.
    public boolean isPatientHavingEpisode(double threshold) {

        Calibration calTest = new Calibration();
        ArrayList<Double> totalResults = calTest.getVarArray(sArray, calTest.calculateMagnitude(xArray, yArray, zArray));

        String thresh = "Threshold Value: " + threshold;
        System.out.println(thresh);

        for (int i = 0; i < totalResults.size(); i++) {
            if (totalResults.get(i) > threshold && totalResults.get(i+1) > threshold && totalResults.get(i+2) > threshold ) {
                System.out.println("Three consecutive results over threshold");
                return true;
            }
        }

        return false;

    }

    // Calculates the mean of an array by iterating through.
    // Will be needed to calculate variance.
    public double calcArrayMean(ArrayList<Double> givenArray, int startValue, int endValue) {
        double total = 0;
        for (int i = startValue; i < endValue ; i++) {
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
        for (int i = startValue; i < endValue; i++ ) {
            currentValue = (givenArray.get(i) - meanOfArray);
            sqDifference = currentValue * currentValue;
            squaredDifferenceArray.add(sqDifference);
        }

        //Average of those squared differences = variance
        variance = calcArrayMean(squaredDifferenceArray, 0, squaredDifferenceArray.size());

        return (variance);
    }

}