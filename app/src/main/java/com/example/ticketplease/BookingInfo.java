package com.example.ticketplease;

public class BookingInfo {
    private String cinemaName;
    private String technology;
    private String date;
    private String time;
    private String movieName;
    private String userID;
    private int[] seats;

    public BookingInfo(String cinemaName, String technology, String date, String time, String movieName, String userID, int[] seats) {
        this.cinemaName = cinemaName;
        this.technology = technology;
        this.date = date;
        this.time = time;
        this.movieName = movieName;
        this.userID = userID;
        this.seats = seats;
    }

    public BookingInfo() {
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int[] getSeats() {
        return seats;
    }

    public void setSeats(int[] seats) {
        this.seats = seats;
    }
}
