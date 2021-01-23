package com.example.ticketplease;

public class HomeFilmListItem {
    private String Title;
    private String pathToPoster;
    HomeFilmListItem(String T, String P){
        Title=T;
        pathToPoster=P;
    }

    public String getTitle() {
        return Title;
    }

    public String getPathToPoster() {
        return pathToPoster;
    }
}
