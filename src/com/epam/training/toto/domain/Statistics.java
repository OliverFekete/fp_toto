package com.epam.training.toto.domain;

public class Statistics {
    private int allMatch = 0;
    private int team01Won = 0;
    private int team02Won = 0;
    private int draw = 0;

    public void increaseTeam01() {
        allMatch++;
        team01Won++;
    }

    public void increaseTeam02() {
        allMatch++;
        team02Won++;
    }

    public void increaseDraw() {
        allMatch++;
        draw++;
    }

    public double getTeam01InPercentage() {
        return (double)team01Won / allMatch * 100;
    }

    public double getTeam02InPercentage() {
        return (double)team02Won / allMatch * 100;
    }

    public double getDrawInPercentage() {
        return (double)draw / allMatch * 100;
    }

    public int getAllMatch() {
        return allMatch;
    }

    public int getTeam01Won() {
        return team01Won;
    }

    public int getTeam02Won() {
        return team02Won;
    }

    public int getDraw() {
        return draw;
    }


}
