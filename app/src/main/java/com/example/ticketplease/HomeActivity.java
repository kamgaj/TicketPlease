package com.example.ticketplease;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {
    ArrayList<HomeFilmListItem> filmsArray = new ArrayList<>();
    ArrayList<HomeFilmListItem> filmsArray2 = new ArrayList<>();
    ArrayList<HomeFilmListItem> filmsArray3 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        ImageView Ticket;
        Ticket = (ImageView) findViewById(R.id.ticketButton);
        Ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TicketActivity.class));
            }
        });
        ImageView Search;
        Search = (ImageView) findViewById(R.id.searchButton);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            }
        });
        ImageView Profile;
        Profile = (ImageView) findViewById(R.id.profileButton);
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });
        getFilms();
        LinearLayout linearLayout;
        linearLayout = findViewById(R.id.FilmsLinearLayout);
        addToView(filmsArray,linearLayout);
        getFilms2();
        LinearLayout linearLayout2;
        linearLayout2 = findViewById(R.id.bestReviewsFilmsLinearLayout);
        addToView(filmsArray2,linearLayout2);
        getFilms3();
        LinearLayout linearLayout3;
        linearLayout3 = findViewById(R.id.DiscountsLinearLayout);
        addToView(filmsArray3,linearLayout3);

    }

    private void getFilms() {
        filmsArray.add(new HomeFilmListItem("Czarny ekran", 5.50, R.drawable.film_poster));
        filmsArray.add(new HomeFilmListItem("Czarny ekran 2", 9.50, R.drawable.film_poster));
        filmsArray.add(new HomeFilmListItem("Czarny ekran 3", 4.5, R.drawable.film_poster));
        filmsArray.add(new HomeFilmListItem("Czarny ekran 4", 13.0, R.drawable.film_poster));
        filmsArray.add(new HomeFilmListItem("Czarny ekran 5", 7.0, R.drawable.film_poster));
        filmsArray.add(new HomeFilmListItem("Czarny ekran 6", 8.0, R.drawable.film_poster));
    }
    private void getFilms2() {
        filmsArray2.add(new HomeFilmListItem("Czarny plakat", 5.50, R.drawable.film_poster));
        filmsArray2.add(new HomeFilmListItem("Czarny plakat 2", 7.50, R.drawable.film_poster));
        filmsArray2.add(new HomeFilmListItem("Czarny plakat 3", 12.50, R.drawable.film_poster));
        filmsArray2.add(new HomeFilmListItem("Czarny plakat 4", 19.50, R.drawable.film_poster));
        filmsArray2.add(new HomeFilmListItem("Czarny plakat 5", 5.21, R.drawable.film_poster));
        filmsArray2.add(new HomeFilmListItem("Czarny plakat 6", 1.50, R.drawable.film_poster));
    }
    private void getFilms3() {
        filmsArray3.add(new HomeFilmListItem("Test 1", 1.50, R.drawable.film_poster));
        filmsArray3.add(new HomeFilmListItem("Test 2", 2.50, R.drawable.film_poster));
        filmsArray3.add(new HomeFilmListItem("Test 3", 3.5, R.drawable.film_poster));
        filmsArray3.add(new HomeFilmListItem("Test 4", 4.0, R.drawable.film_poster));
        filmsArray3.add(new HomeFilmListItem("Test 5", 5.0, R.drawable.film_poster));
        filmsArray3.add(new HomeFilmListItem("Test 6", 6.0, R.drawable.film_poster));
    }

    public void addToView(ArrayList<HomeFilmListItem> listFilms, LinearLayout linearLayout) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for (int i = 0; i < listFilms.size(); i++) {
            View view = layoutInflater.inflate(R.layout.home_page_item, linearLayout, false);
            TextView title = view.findViewById(R.id.MovieTitle);
            TextView price = view.findViewById(R.id.Price);
            ImageView poster = view.findViewById(R.id.Poster);
            title.setText(listFilms.get(i).Title);
            poster.setImageResource(listFilms.get(i).Poster);
            price.setText(String.valueOf(listFilms.get(i).Price));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this, DescriptionActivity.class));
                }
            });
            linearLayout.addView(view);
        }
    }
}