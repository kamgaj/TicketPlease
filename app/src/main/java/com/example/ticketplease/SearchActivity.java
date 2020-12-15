package com.example.ticketplease;

import android.content.Intent;
import android.icu.text.DateFormatSymbols;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.resources.TextAppearance;

import java.util.ArrayList;
import java.util.Arrays;

import io.opencensus.resource.Resource;

public class SearchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);
        ImageView Ticket;
        Ticket = (ImageView) findViewById(R.id.ticketButton);
        Ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, TicketActivity.class));
            }
        });
        ImageView Home;
        Home = (ImageView) findViewById(R.id.homeButton);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, HomeActivity.class));
            }
        });
        ImageView Profile;
        Profile = (ImageView) findViewById(R.id.profileButton);
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            }
        });
        ListView searchView;
        //String[] Titles;
        searchView=findViewById(R.id.searchListView);
        String Titles[] = {"Title","Title2","Title3","Title4","Title5","Title6","Title7","Title8","Title9","Title10","Title11","Title12"}; //new DateFormatSymbols().getMonths();
        ArrayAdapter<String> titlesArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,Titles);
        searchView.setAdapter(titlesArray);
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(SearchActivity.this,DescriptionActivity.class);
                startActivity(i);
            }
        });
    }
}
