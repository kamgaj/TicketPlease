package com.example.ticketplease;

public class ProfileFilmListItem {
    private String Title;
    private String Description;
    private String Poster;
    private String Id;
    private String Date;
    private String Time;
    private String Seats;
    private String Cinema;
    private String numberTickets;
    ProfileFilmListItem(String T, String D, String P, String I,String Date, String Time,String Cinema, String numberTickets, String Seats){
        Title=T;
        Description=D;
        Poster=P;
        Id=I;
        this.Date=Date;
        this.Time=Time;
        this.Cinema=Cinema;
        this.numberTickets=numberTickets;
        this.Seats=Seats;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getPoster() {
        return Poster;
    }

    public String getId() {
        return Id;
    }

    public String getSeats() {
        return Seats;
    }

    public String getCinema() {
        return Cinema;
    }

    public String getNumberTickets() {
        return numberTickets;
    }
}
