package com.example.projetofinalcm2022.models;

public class History {

    private long id;
    private String mode;
    private String date;
    private String winner;
    private int numberOfShots;
    private int boatHits;
    private int sunkenShips;

    public History(long id, String mode, String date, String winner, int numberOfShots,
                   int boatHits, int sunkenShips) {
        this.id = id;
        this.mode = mode;
        this.date = date;
        this.winner = winner;
        this.numberOfShots = numberOfShots;
        this.boatHits = boatHits;
        this.sunkenShips = sunkenShips;
    }
    public History(String mode, String date, String winner, int numberOfShots,
                   int boatHits, int sunkenShips) {
        this.id = id;
        this.mode = mode;
        this.date = date;
        this.winner = winner;
        this.numberOfShots = numberOfShots;
        this.boatHits = boatHits;
        this.sunkenShips = sunkenShips;
    }

    public History() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getNumberOfShots() {
        return numberOfShots;
    }

    public void setNumberOfShots(int numberOfShots) {
        this.numberOfShots = numberOfShots;
    }

    public int getBoatHits() {
        return boatHits;
    }

    public void setBoatHits(int boatHits) {
        this.boatHits = boatHits;
    }

    public int getSunkenShips() {
        return sunkenShips;
    }

    public void setSunkenShips(int sunkenShips) {
        this.sunkenShips = sunkenShips;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", mode='" + mode + '\'' +
                ", date='" + date + '\'' +
                ", winner='" + winner + '\'' +
                ", numberOfShots=" + numberOfShots +
                ", boatHits=" + boatHits +
                ", sunkenShips=" + sunkenShips +
                '}';
    }
}
