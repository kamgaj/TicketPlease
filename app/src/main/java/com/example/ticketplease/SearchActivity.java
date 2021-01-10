package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchActivity extends AppCompatActivity {
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();
    ArrayAdapter<String> titlesArray;
    ArrayAdapter<String> genresArray;
    ListView searchView;
    RadioButton Title;
    RadioButton Genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        Genre = (RadioButton) findViewById(R.id.offer);
        Title = (RadioButton) findViewById(R.id.search);

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


        searchView = findViewById(R.id.searchListView);
        EditText search=findViewById(R.id.SearchTextList);
        search.setEnabled(false);


        getStringFromXML();




        Title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setAdapter(titlesArray);
                search.setEnabled(true);

                goToSearchedMovieDescription();
                }

        });

        Genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringFromXML();
                search.getText().clear();
                search.setEnabled(false);

                queryGenresFromFirebase();
                }


        });

        Genre.performClick(); //It is necessary, because at create of this activity this line of code clicks in Genre radio button so it allows
                                //to use on click listener without a initial click

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {


                if(search.getText().length()>=3) {
                    String start = search.getText().toString();
                    start = start.substring(0, 1).toUpperCase() + start.substring(1);
                    String end = search.getText().toString() + "\uf8ff";
                    end = end.substring(0, 1).toUpperCase() + end.substring(1);
                    db.collection("Movies")
                            .orderBy("Title")
                            .startAt(start)
                            .endAt(end)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        List<String> titles = new ArrayList<>();
                                        for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            titles.add(document.getString("Title"));
                                        }
                                        titlesArray =  new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,titles);
                                        searchView.setAdapter(titlesArray);
                                    }
                                }
                            });
                } else {
                    titlesArray = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1,
                            new ArrayList<>());
                    searchView.setAdapter(titlesArray);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


    }

    private void getStringFromXML() {
        String[] genresFromXML = getResources().getStringArray(R.array.genres);
        genresArray = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, genresFromXML);
        searchView.setAdapter(genresArray);
    }

    private void queryGenresFromFirebase() {
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedGenre = (String) parent.getItemAtPosition(position);
                db.collection("Movies")
                        .whereArrayContains("Genres", selectedGenre)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    List<String> titles = new ArrayList<>();
                                    for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        titles.add(document.getString("Title"));
                                    }
                                    genresArray = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, titles);
                                    searchView.setAdapter(genresArray);

                                    goToSearchedMovieDescription();
                                }
                            }
                        });

            }
        });
    }

    private void goToSearchedMovieDescription() {
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(SearchActivity.this, DescriptionActivity.class);
                intent.putExtra("Movie_title", selectedItem);
                startActivity(intent);
            }
        });
    }
}
