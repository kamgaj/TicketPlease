package com.example.ticketplease;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;


public class HomeActivity extends AppCompatActivity {
    ArrayList<HomeFilmListItem> filmsArray = new ArrayList<>();
    ArrayList<HomeFilmListItem> filmsArray2 = new ArrayList<>();
    ArrayList<HomeFilmListItem> discountsArray = new ArrayList<>();
    Chip search;
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();

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
        search = findViewById(R.id.chip4);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSearch = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(goToSearch);
            }
        });
        //Displaying Movies
        getNewestFilms();
        getTopRatedFilms();
        getDiscounts();


    }

    private void getNewestFilms() {

        db.collection("Movies")
                .orderBy("Release_date", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String path = document.getString("Poster_link");
                                filmsArray.add(new HomeFilmListItem(document.getString("Title"),  path));
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

        db.collection("Movies")
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String path = document.getString("Poster_link");
                                filmsArray2.add(new HomeFilmListItem(document.getString("Title"),  path));
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

    private void getDiscounts() {

        db.collection("Discounts")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String path = document.getString("Image_link");
                                discountsArray.add(new HomeFilmListItem(document.getString("Title"),  path));
                            }
                            LinearLayout linearLayout3;
                            linearLayout3 = findViewById(R.id.DiscountsLinearLayout);
                            addToViewDiscount(discountsArray,linearLayout3);
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
            ImageView poster = view.findViewById(R.id.Poster);
            title.setText(listFilms.get(i).getTitle());

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference ref = storage.getReferenceFromUrl(listFilms.get(i).getPathToPoster());
            ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    poster.setImageBitmap(bitmap);
                }
            });

            int finalI = i;
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, DescriptionActivity.class);
                    intent.putExtra("Movie_title", listFilms.get(finalI).getTitle());
                    startActivity(intent);
                }
            });
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, DescriptionActivity.class);
                    intent.putExtra("Movie_title", listFilms.get(finalI).getTitle());
                    startActivity(intent);
                }
            });
            linearLayout.addView(view);
        }
    }
    public void addToViewDiscount(ArrayList<HomeFilmListItem> listDiscounts, LinearLayout linearLayout) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for (int i = 0; i < listDiscounts.size(); i++) {
            View view = layoutInflater.inflate(R.layout.home_page_item, linearLayout, false);
            TextView title = view.findViewById(R.id.MovieTitle);
            ImageView poster = view.findViewById(R.id.Poster);
            title.setText(listDiscounts.get(i).getTitle());

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference ref = storage.getReferenceFromUrl(listDiscounts.get(i).getPathToPoster());
            ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    poster.setImageBitmap(bitmap);
                }
            });

            int finalI = i;
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, DiscountActivity.class);
                    intent.putExtra("Discount_title", listDiscounts.get(finalI).getTitle());
                    startActivity(intent);
                }
            });
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, DiscountActivity.class);
                    intent.putExtra("Movie_title", listDiscounts.get(finalI).getTitle());
                    startActivity(intent);
                }
            });
            linearLayout.addView(view);
        }
    }

    @Override
    public void onBackPressed() {

        int requestCode = getIntent().getIntExtra("disableBackButton", 1234);

        if(requestCode == 7312) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }else {
            super.onBackPressed();
            finishAffinity();
        }
    }

}