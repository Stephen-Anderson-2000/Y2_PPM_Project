package com.example.ppm_project;
import java.util.ArrayList;
//Using ArrayList, this is an array of non-fixed length.
//Data read from the csv file will need to be added to an ArrayList
//This can be done by iterating through each line, parsing the correct
//part as a double, then using arrayList.add(i).

public class AccelerationData
{
    // Set data as a struct?
    //accelerationData;

    public void setAccelData()
    {
        // Stub definition. Need to find a way to fetch the csv file and then turn it into the
        // acceleration data as well
    }

    //public /*type goes here*/ getAccelData() { return accelerationData; }

    public void analyseData(ArrayList<Double> xArray, ArrayList<Double> yArray, ArrayList<Double> zArray)
    {
        calcVariance(xArray);
        calcVariance(yArray);
        calcVariance(zArray);

        //Must now compare against a 'threshold' value.
        //The variance of the data gathered was:
        //x (NORMAL): 0.0315 (ERRATIC): 0.383
        //y (NORMAL): 0.127  (ERRATIC): 0.35
        //z (NORMAL): 0.042  (ERRATIC): 0.213

        //Perhaps somewhere around 0.2 as a minimum threshold for erratic movement?
    }

    //Calculates the mean of an array by iterating through.
    //Will be needed to calculate variance.
    public double calcArrayMean(ArrayList<Double> givenArray) {
        double total = 0;

        for (int i = 0; i < givenArray.size(); i++) {
            total = total + givenArray.get(i);
        }

        double arrayMean = total / givenArray.size();
        return (arrayMean);
    }

    //Calculates the variance of an array.
    public double calcVariance(ArrayList<Double> givenArray) {
        ArrayList<Double> squaredDifferenceArray = new ArrayList<Double>();
        double sqDifference;
        double variance;
        double currentValue;

        //Calculate the mean of the array first
        double meanOfArray = calcArrayMean(givenArray);

        //Subtract the mean from each value in the array
        //Square the result
        for (int i = 0; i < givenArray.size(); i++ ) {
            currentValue = (givenArray.get(i) - meanOfArray);
            sqDifference = currentValue * currentValue;
            squaredDifferenceArray.add(sqDifference);
        }

        //Average of those squared differences = variance
        variance = calcArrayMean(squaredDifferenceArray);

        return (variance);
    }

}