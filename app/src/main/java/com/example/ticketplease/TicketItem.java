package com.example.ticketplease;

import java.util.List;

public class TicketItem {
    private String id;
    private String title;
    private String date;
    private String time;
    private int numberOfTickets;
    private String cinemaName;
    private List<Integer> seats;
    TicketItem(String id,String title,String date,String time,int numberOfTickets,String cinemaName,List<Integer> seats){
        this.id=id;
        this.title=title;
        this.date=date;
        this.time=time;
        this.numberOfTickets=numberOfTickets;
        this.cinemaName=cinemaName;
        this.seats=seats;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public List<Integer> getSeats() {
        return seats;
    }
}
