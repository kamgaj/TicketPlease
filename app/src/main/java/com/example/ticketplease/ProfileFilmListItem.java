package com.example.ticketplease;

public class ProfileFilmListItem {
    String Title;
    String Description;
    String Poster;
    String Id;
    String Date;
    String Time;
    String Seats;
    String Cinema;
    String numberTickets;
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
}
