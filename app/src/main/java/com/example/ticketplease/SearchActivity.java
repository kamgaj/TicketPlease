package com.example.ticketplease;

import android.content.Intent;
import android.icu.text.DateFormatSymbols;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.resources.TextAppearance;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;



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
        boolean isSearchPressed=true;
        //String[] Titles;
        searchView=findViewById(R.id.searchListView);
        String Titles[] = {"Tytuł","Tytuł2","Tytuł3","Tytuł4","Tytuł5","Tytuł6","Tytuł7","Tytuł8","Tytuł9","Tytuł10","Tytuł11","Tytuł12"};
        ArrayAdapter<String> titlesArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,Titles);
        String Offers[] = {"Tytuł z gatunku","Tytuł2 z gatunku","Tytuł3 z gatunku","Tytuł4 z gatunku","Tytuł5 z gatunku","Tytuł6 z gatunku","Tytuł7 z gatunku","Tytuł8 z gatunku","Tytuł9 z gatunku","Tytuł10 z gatunku","Tytuł11 z gatunku","Tytuł12 z gatunku"};
        ArrayAdapter<String> offersArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,Offers);
        searchView.setAdapter(offersArray);
        /*searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(SearchActivity.this,DescriptionActivity.class);
                startActivity(i);
            }
        });*/
        RadioButton Search = (RadioButton) findViewById(R.id.search);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setAdapter(titlesArray);
            }
        });
        RadioButton Offer = (RadioButton) findViewById(R.id.offer);
        Offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setAdapter(offersArray);
            }
        });

        EditText search=findViewById(R.id.SearchTextList);
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                if(search.getText().length()>=3)Toast.makeText(getApplicationContext(), search.getText(), Toast.LENGTH_LONG).show();

                // yourEditText...
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


    }
}
