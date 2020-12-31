package com.epam.training.toto.service;

import com.epam.training.toto.domain.Hit;
import com.epam.training.toto.domain.Outcome;
import com.epam.training.toto.domain.Round;
import com.epam.training.toto.domain.Statistics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.lang.Integer.parseInt;

public class TotoService {
    private static List<Round> rounds = new ArrayList<Round>();
    private static int largestPriceEver = 0;
    private static Statistics statistics = new Statistics();
    private static int roundsNumber = 0;

    //read input data from file
    public static void input(String oneLineStr) {
        String[] splitString = oneLineStr.split(";");
        Round oneRound = new Round();
        roundsNumber++;
        //set year
        oneRound.setYear(parseInt(splitString[0]));
        //set week
        oneRound.setWeek(parseInt(splitString[1]));
        //set roundOfWeek
        if (splitString[2].equals("2")) {
            oneRound.setRoundOfWeek(2);
        }
        else {
            oneRound.setRoundOfWeek(1);
        }
        //set date
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            if (!splitString[3].equals("")) {
                splitString[3] = splitString[3].replaceAll("\\.", "/");
                int strLength = splitString[3].length();
                if (splitString[3].charAt(strLength - 1) != '/') {
                    oneRound.setDate(LocalDate.parse(splitString[3], dateTimeFormatter));
                } else {
                    oneRound.setDate(LocalDate.parse(splitString[3].substring(0, strLength - 1), dateTimeFormatter));
                }
            } else {
                WeekFields weekFields = WeekFields.of(Locale.forLanguageTag("hu-HU"));
                LocalDate localDate = LocalDate.ofYearDay(oneRound.getYear(), 1);
                localDate = localDate.with(weekFields.weekOfYear(), oneRound.getWeek());
                localDate = localDate.with(weekFields.dayOfWeek(), oneRound.getRoundOfWeek());
                oneRound.setDate(localDate);
            }
        }
        catch (Exception e) {
            System.out.println("Invalid date");
        }
        //set hits
        List<Hit> oneRoundHits = new ArrayList<Hit>();
        for (int i = 14, j = 4, k = 5; i >= 10; i--, j += 2, k += 2) {
            String prize_temp = splitString[k].replaceAll("\\s+", "");
            prize_temp = prize_temp.substring(0, prize_temp.length() - 2);
            int wagers = parseInt(splitString[j]);
            int prizeInt = parseInt(prize_temp);
            Hit hit_temp = new Hit(i, wagers, prizeInt);
            oneRoundHits.add(hit_temp);
            if (prizeInt > largestPriceEver) {
                largestPriceEver = prizeInt;
            }
        }
        oneRound.setHits(oneRoundHits);
        //set outcomes
        List<Outcome> oneRoundOutcomes = new ArrayList<Outcome>();
        for (int i = 0; i < 14; i++) {
            switch (splitString[14 + i].charAt(splitString[14 + i].length() - 1)) {
                case '1':
                    oneRoundOutcomes.add(Outcome._1);
                    statistics.increaseTeam01();
                    break;
                case '2':
                    oneRoundOutcomes.add(Outcome._2);
                    statistics.increaseTeam02();
                    break;
                case 'X':
                case 'x':
                    oneRoundOutcomes.add(Outcome.X);
                    statistics.increaseDraw();
                    break;
                default: break;
            }
        }
        oneRound.setOutcomes(oneRoundOutcomes);
        rounds.add(oneRound);
    }

    //print the largest prize ever recorded to the console
    public static void printLargestPriceEver() {
        String largestPriceEverStr = String.format("%,d", largestPriceEver);
        System.out.println("The largest prize ever recorded: " + largestPriceEverStr + " Ft");
    }

    //print the aggregated distribution of the 1/2/X results of all rounds to the console
    public static void printStatistics() {
        System.out.format("Statistics: team #1 won: %.2f %%, team #2 won: %.2f %%, draw: %.2f %%%n", statistics.getTeam01InPercentage(), statistics.getTeam02InPercentage(), statistics.getDrawInPercentage());
    }

    //calculate and print the hits and amount for the wager, read the data from the console
    public static void getTipsFromUser() {
        Scanner scanner = new Scanner(System.in);
        String date_temp = "";
        String outcomes_temp = "";
        LocalDate date_tempDate = LocalDate.now();
        List<Outcome> outcomes_tempList = new ArrayList<Outcome>();

        System.out.print("Enter date: ");
        date_temp = scanner.nextLine();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        date_temp = date_temp.replaceAll("[\\.\\-\\;\\s]", "/");
        if (date_temp.charAt(date_temp.length() - 1) == '/') {
            date_temp = date_temp.substring(0, date_temp.length() - 1);
        }
        try {
            date_tempDate = LocalDate.parse(date_temp, dateTimeFormatter);
        }
        catch (Exception e) {
            System.out.println("Invalid date (try: yyyy-mm-dd)");
            getTipsFromUser();
            return;
        }

        System.out.print("Enter outcomes: ");
        outcomes_temp = scanner.nextLine();

        for (int i = 0; i < outcomes_temp.length(); i++) {
            switch (outcomes_temp.charAt(i)) {
                case '1':
                    outcomes_tempList.add(Outcome._1);
                    break;
                case '2':
                    outcomes_tempList.add(Outcome._2);
                    break;
                case 'X':
                case 'x':
                    outcomes_tempList.add(Outcome.X);
                    break;
                default: break;
            }
        }

        checkHits(date_tempDate, outcomes_tempList);
    }

    private static void checkHits(LocalDate date, List<Outcome> outcomes) {
        String amount_temp = "0";
        int amount_number = 0;
        int hits_temp = 0;

        Optional<Round> roundSearch = rounds.stream()
                .filter(round -> round.getDate().equals(date))
                .findFirst();

        if (roundSearch.isPresent()) {
            Round chosenRound = roundSearch.get();
            for (int i = 0; i < outcomes.size() && i < 14; i++) {
                if (outcomes.get(i).equals(chosenRound.getOutcomes().get(i))) {
                    hits_temp++;
                }
            }

            if (hits_temp >= 10) {
                for (int i = 0; i < 5; i++) {
                    if (chosenRound.getHits().get(i).getHitCount() == hits_temp) {
                        amount_number = chosenRound.getHits().get(i).getPrize();
                    }
                }
            }

            amount_temp = String.format("%,d", amount_number);

            System.out.println("Result: hits: " + hits_temp + ", amount: " + amount_temp + " Ft");
        }
        else {
            System.out.println("No date found");
        }

    }

}
