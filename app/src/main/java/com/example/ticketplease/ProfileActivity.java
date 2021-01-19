package com.example.ticketplease;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private final static String TAG = "ProfileActivity";
    ListAdapter listAdapter;
    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null){
                //Do anything here which needs to be done after signout is complete
                Intent logoutIntent = new Intent(ProfileActivity.this,LoginActivity.class);
                logoutIntent.putExtra("logoutCode", 2137);
                startActivity(logoutIntent);
            }

        }
    };
    private StorageReference storageReference;
    private String uID;
    private ImageView profilePicture;
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();

    ArrayList<ProfileFilmListItem> filmsArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        TextView username=findViewById(R.id.NickName);
        String login= (Objects.requireNonNull(firebaseAuth.getCurrentUser())).getDisplayName()+"";
        if (login.length()!= 0 && !login.equals("null")) {
            username.setText(login);
        }
        else {
            username.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());
        }
        TextView logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        uID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        //Downloading user picture from Firebase
        StorageReference profileRef = storageReference.child("User_profile_pictures/" + uID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().into(profilePicture);
            }
        });

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

        getBookedFilms();
        TextView settingsProfile;
        settingsProfile=findViewById(R.id.settingsProfile);
        settingsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            }
        });
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        profilePicture = findViewById(R.id.ProfilePicture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGaleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGaleryIntent, 2137);
            }
        });

        RadioButton Watched = (RadioButton) findViewById(R.id.watched);
        Watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsArray.clear();
                getWatchedFilms();
            }
        });
        RadioButton Booked = (RadioButton) findViewById(R.id.booked);
        Booked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsArray = new ArrayList<>();
                getBookedFilms();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2137) {
            if(resultCode == Activity.RESULT_OK) {
                Uri imageUri = Objects.requireNonNull(data).getData();

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("User_profile_pictures/" + uID + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePicture);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Nie udało się pobrać zdjęcia", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getWatchedFilms() {
        storageReference = FirebaseStorage.getInstance().getReference();
//        List<String> movies = new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        String currentDate = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        try {
            Date date =  formatter.parse(currentDate);

            db.collection("Bookings")
                    .whereEqualTo("userID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    String temp = document.getString("date");
                                    try {
                                        Date queryDate = formatter.parse(Objects.requireNonNull(temp));
                                        if(Objects.requireNonNull(date).after(queryDate)) {
//                                            movies.add(document.getString("movieName"));
                                            String asd = document.getString("movieName");
                                            try {
                                                db.collection("Movies")
//                                                        .whereIn("Title", movies)
                                                        .whereEqualTo("Title", asd)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                        String path = document.getString("Poster_link");
                                                                        String id = document.getId();
                                                                        filmsArray.add(new ProfileFilmListItem(document.getString("Title"), document.getString("Description"), path, id));
                                                                    }
                                                                    PrintWatched(0);
                                                                } else {
                                                                    Log.d(TAG, "Watched films, Movie Collection Query FAILS");
                                                                }
                                                            }
                                                        });
                                            } catch (IllegalArgumentException iae) {
                                                Log.e(TAG, "Watched array was empty");
                                                filmsArray = new ArrayList<>();
                                                PrintWatched(0);
                                            }
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }



                            } else {
                                Log.d(TAG, "Watched films, Bookings Collection Query FAILS");
                            }
                        }
                    });

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getBookedFilms() {
        storageReference = FirebaseStorage.getInstance().getReference();
//        List<String> movies = new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        String currentDate = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        try {
            Date date =  formatter.parse(currentDate);

            db.collection("Bookings")
                    .whereEqualTo("userID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    String temp = document.getString("date");
                                    try {
                                        Date queryDate = formatter.parse(Objects.requireNonNull(temp));
                                        if(Objects.requireNonNull(date).before(queryDate) || date.equals(queryDate)) {
//                                            movies.add(document.getString("movieName"));
                                            String test = document.getString("movieName");
                                            try {
                                                db.collection("Movies")
//                                                        .whereIn("Title", movies)
                                                        .whereEqualTo("Title", test)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                        String path = document.getString("Poster_link");
                                                                        String id = document.getId();
                                                                        filmsArray.add(new ProfileFilmListItem(document.getString("Title"), document.getString("Description"), path, id));
                                                                    }
                                                                    PrintWatched(1);
                                                                } else {
                                                                    Log.d(TAG, "Watched films, Movie Collection Query FAILS");
                                                                }
                                                            }
                                                        });
                                            } catch (IllegalArgumentException iae) {
                                                Log.e(TAG, "Booked array was empty");
                                                filmsArray = new ArrayList<>();
                                                PrintWatched(1);
                                            }
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }


                            } else {
                                Log.d(TAG, "Watched films, Bookings Collection Query FAILS");
                            }
                        }
                    });

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    void PrintWatched(int mode){
        ListView films;
        films=findViewById(R.id.ListFilms);
        listAdapter = new ProfileListView(this,filmsArray,mode);
        films.setAdapter(listAdapter);
    }

}
