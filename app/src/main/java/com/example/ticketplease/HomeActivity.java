package com.example.ticketplease;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class HomeActivity extends AppCompatActivity {
    ArrayList<HomeFilmListItem> filmsArray = new ArrayList<>();
    ArrayList<HomeFilmListItem> filmsArray2 = new ArrayList<>();
    ArrayList<HomeFilmListItem> filmsArray3 = new ArrayList<>();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionRef;
    private StorageReference storageReference;
    private static final String TAG = "HomeActivity";

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



        //Displaying Movies
        getNewestFilms();
        getTopRatedFilms();
        getDiscountedFilms();


    }

    private void getNewestFilms() {
        storageReference = FirebaseStorage.getInstance().getReference();
        collectionRef = db.collection("Movies");

        db.collection("Movies")
                .orderBy("Release_date", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                filmsArray.add(new HomeFilmListItem(document.getString("Title"), 5.50, R.drawable.film_poster));
                            }
                            LinearLayout linearLayout;
                            linearLayout = findViewById(R.id.FilmsLinearLayout);
                            addToView(filmsArray, linearLayout);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getTopRatedFilms() {
        storageReference = FirebaseStorage.getInstance().getReference();
        collectionRef = db.collection("Movies");

        db.collection("Movies")
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                filmsArray2.add(new HomeFilmListItem(document.getString("Title"), 5.50, R.drawable.film_poster));
                            }
                            LinearLayout linearLayout2;
                            linearLayout2 = findViewById(R.id.bestReviewsFilmsLinearLayout);
                            addToView(filmsArray2,linearLayout2);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getDiscountedFilms() {
        storageReference = FirebaseStorage.getInstance().getReference();
        collectionRef = db.collection("Movies");

        db.collection("Movies")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                filmsArray3.add(new HomeFilmListItem(document.getString("Title"), 5.50, R.drawable.film_poster));
                            }
                            LinearLayout linearLayout3;
                            linearLayout3 = findViewById(R.id.DiscountsLinearLayout);
                            addToView(filmsArray3,linearLayout3);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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

            int finalI = i;
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, DescriptionActivity.class);
                    intent.putExtra("Movie_title", listFilms.get(finalI).Title);
                    startActivity(intent);
                }
            });
            linearLayout.addView(view);
        }
    }

}