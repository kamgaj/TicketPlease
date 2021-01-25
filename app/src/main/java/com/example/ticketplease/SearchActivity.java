package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    RadioButton title;
    RadioButton genre;
    boolean ifGenre = false;
    ImageView ticket;
    ImageView home;
    ImageView profile;
    EditText search;
    public static final String TITLE_WORD = "Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        genre = findViewById(R.id.offer);
        title = findViewById(R.id.search);
        ticket = findViewById(R.id.ticketButton);
        home = findViewById(R.id.homeButton);
        profile = findViewById(R.id.profileButton);
        searchView = findViewById(R.id.searchListView);
        search = findViewById(R.id.SearchTextList);

        search.setEnabled(false);

        ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, TicketActivity.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, HomeActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setAdapter(titlesArray);
                search.setEnabled(true);
                search.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
                ifGenre=false;
                goToSearchedMovieDescription();
            }
        });

        genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.getText().clear();
                search.setEnabled(false);
                queryGenresFromFirebase();
                getStringFromXML();
            }
        });

        title.performClick();

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(search.getText().length()>=3) {
                    String start = search.getText().toString();
                    start = start.substring(0, 1).toUpperCase() + start.substring(1);
                    String end = search.getText().toString() + "\uf8ff";
                    end = end.substring(0, 1).toUpperCase() + end.substring(1);
                    db.collection("Movies")
                            .orderBy(TITLE_WORD)
                            .startAt(start)
                            .endAt(end)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        List<String> titles = new ArrayList<>();
                                        for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            titles.add(document.getString(TITLE_WORD));
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

    @Override
    public void onBackPressed(){
        if(ifGenre){
            genre.performClick();
            ifGenre = false;
        }
        else{
            finish();
        }
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
                ifGenre=true;
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
                                        titles.add(document.getString(TITLE_WORD));
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
