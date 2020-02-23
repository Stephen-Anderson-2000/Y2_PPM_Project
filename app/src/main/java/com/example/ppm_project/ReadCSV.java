package com.example.ppm_project;

import java.io.BufferedReader;
import java.io.FileReader;

public class ReadCSV {

    public String readFile(String actualFilePath) {
        StringBuilder allData = new StringBuilder();
        try {
            String row;
            BufferedReader csvReader = new BufferedReader(new FileReader(actualFilePath));
            while ((row = csvReader.readLine()) != null) {
                String[] csvData = row.split(",");
                for(int i = 0; i < csvData.length; i++){
                    allData.append(csvData[i]).append(" ");
                }
            }
        } catch (java.io.IOException s) {
                System.out.println(s.getMessage());
        }
        return allData.toString();
    }

}
