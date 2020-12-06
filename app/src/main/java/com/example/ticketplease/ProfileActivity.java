package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    ArrayList<ProfileFilmListItem> filmsArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        ImageView Ticket;
        Ticket = (ImageView) findViewById(R.id.ticketButton);
        Ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, TicketActivity.class));
            }
        });
        ImageView Search;
        Search = (ImageView) findViewById(R.id.searchButton);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
            }
        });
        ImageView Home;
        Home = (ImageView) findViewById(R.id.homeButton);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });
        /*ListView films;
        String[] Titles;
        films=findViewById(R.id.ListFilms);
        Titles = new DateFormatSymbols().getMonths();
        ArrayAdapter<String> titlesArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,Titles);
        films.setAdapter(titlesArray);*/
        getFilms();
        ListView films;
        ListAdapter listAdapter;
        films=findViewById(R.id.ListFilms);
        listAdapter = new ProfileListView(this,filmsArray);
        films.setAdapter(listAdapter);
    }

    private void getFilms() {
        filmsArray.add(new ProfileFilmListItem("Czarny ekran","Film bez wizji",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 2","Film bez wizji. Kolejna część oscarowej produkcji",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 3","Film bez wizji. Niektórzy myślą, że to cały czas pierwsza część",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 4","Film bez wizji. Niektórzy myślą, że to cały czas pierwsza część",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 5","Film bez wizji. Niektórzy myślą, że to cały czas pierwsza część",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 6","Film bez wizji. Niektórzy myślą, że to cały czas pierwsza część",R.drawable.small_poster));
    }
}
