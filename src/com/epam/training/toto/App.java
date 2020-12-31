package com.epam.training.toto;

import com.epam.training.toto.service.TotoService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class App {

    public static void main(String[] args) {
	    // Entry point
        String path = "D:\\Programming\\Java\\EPAM_mentoring\\Lesson01\\Exercises\\Toto.csv";
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(path))){ //try with resources (automatically close the file when exit the block

            while((line = br.readLine()) != null) {
                TotoService.input(line);
                //String[] splitStr = line.split(";");
                //System.out.println(splitStr[3]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        TotoService.printLargestPriceEver();
        TotoService.printStatistics();

        TotoService.getTipsFromUser();
    }
}
