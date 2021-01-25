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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private final static String TAG = "ProfileActivity";
    ListAdapter listAdapter;
    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null){
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
        StorageReference profileRef = storageReference.child("User_profile_pictures/" + uID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().into(profilePicture);
            }
        });

        ImageView Ticket;
        Ticket = findViewById(R.id.ticketButton);
        Ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, TicketActivity.class));
            }
        });
        ImageView Search;
        Search = findViewById(R.id.searchButton);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
            }
        });
        ImageView Home;
        Home = findViewById(R.id.homeButton);
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

        RadioButton Watched = findViewById(R.id.watched);
        Watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsArray.clear();
                getWatchedFilms();
            }
        });
        RadioButton Booked = findViewById(R.id.booked);
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
        List<String> movies = new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        String currentDate = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        LocalDateTime localDateTime=LocalDateTime.now();
        db.collection("Bookings")
                .whereEqualTo("userID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            filmsArray= new ArrayList<>();
                            printWatched(0);
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String dateSource = document.getString("date");
                                String time = document.getString("time");
                                List<String> items = Arrays.asList(Objects.requireNonNull(dateSource).split("\\."));
                                if(items.get(0).length()==1){
                                    items.set(0,"0"+items.get(0));
                                }
                                if(items.get(1).length()==1){
                                    items.set(1,"0"+items.get(1));
                                }
                                String date=String.join(".",items);
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                LocalDateTime localDateTime1=LocalDateTime.of(LocalDate.parse(date.replaceAll("\\.","/"), formatter), LocalTime.parse(time));
                                    if(localDateTime.isAfter(localDateTime1)) {
                                        String asd = document.getString("movieName");
                                        try {
                                            db.collection("Movies")
                                                    .whereEqualTo("Title", asd)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                    String path = document.getString("Poster_link");
                                                                    String id = document.getId();
                                                                    if(!movies.contains(document.getString("Title"))) {
                                                                        movies.add(document.getString("Title"));
                                                                        filmsArray.add(new ProfileFilmListItem(document.getString("Title"), document.getString("Description"), path, id, date, null,null,null,null));
                                                                    }
                                                                }
                                                                sortMoviesUsingDateDescending(filmsArray);
                                                                printWatched(0);
                                                            } else {
                                                                Log.d(TAG, "Watched films, Movie Collection Query FAILS");
                                                            }
                                                        }
                                                    });
                                        } catch (IllegalArgumentException iae) {
                                            Log.e(TAG, "Watched array was empty");
                                            filmsArray = new ArrayList<>();
                                            printWatched(0);
                                        }
                                    }
                            }

                        } else {
                            Log.d(TAG, "Watched films, Bookings Collection Query FAILS");
                        }
                    }
                });
    }

    private void getBookedFilms() {
        storageReference = FirebaseStorage.getInstance().getReference();
        Calendar calendar=Calendar.getInstance();
        String currentDate = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            LocalDateTime localDateTime=LocalDateTime.now();
            db.collection("Bookings")
                    .whereEqualTo("userID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                filmsArray= new ArrayList<>();
                                printWatched(0);
                                for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    String dateSource = document.getString("date");
                                    String time = document.getString("time");
                                    List<String> items = Arrays.asList(Objects.requireNonNull(dateSource).split("\\."));
                                    if(items.get(0).length()==1){
                                        items.set(0,"0"+items.get(0));
                                    }
                                    if(items.get(1).length()==1){
                                        items.set(1,"0"+items.get(1));
                                    }
                                    String date=String.join(".",items);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                    LocalDateTime localDateTime1=LocalDateTime.of(LocalDate.parse(date.replaceAll("\\.","/"), formatter), LocalTime.parse(time));
                                        if (localDateTime.isBefore(localDateTime1)) {
                                            String test = document.getString("movieName");
                                            String cinemaName = document.getString("cinemaName");
                                            List<Long> seatsL = (List<Long>) document.get("seats");
                                            List<Integer> seatsInt = Objects.requireNonNull(seatsL).stream()
                                                    .map(Long::intValue)
                                                    .collect(Collectors.toList());
                                            StringBuilder allSeats = new StringBuilder();
                                            for (int i = 0; i < seatsInt.size(); i++) {
                                                allSeats.append((seatsInt.get(i) + 1));
                                                if (i != seatsInt.size() - 1) {
                                                    allSeats.append(", ");
                                                }
                                            }
                                            String id = document.getId();
                                            try {
                                                String finalAllSeats = allSeats.toString();
                                                db.collection("Movies")
                                                        .whereEqualTo("Title", test)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                        String path = document.getString("Poster_link");
                                                                        filmsArray.add(new ProfileFilmListItem(document.getString("Title"), document.getString("Description"), path, id, date, time, cinemaName, String.valueOf(seatsInt.size()), finalAllSeats));
                                                                    }
                                                                    sortMoviesUsingDateAndTime(filmsArray);
                                                                    printWatched(1);
                                                                } else {
                                                                    Log.d(TAG, "Watched films, Movie Collection Query FAILS");
                                                                }
                                                            }
                                                        });
                                            } catch (IllegalArgumentException iae) {
                                                Log.e(TAG, "Booked array was empty");
                                                filmsArray = new ArrayList<>();
                                                printWatched(1);
                                            }
                                        }
                                }
                            } else {
                                Log.d(TAG, "Watched films, Bookings Collection Query FAILS");
                            }
                        }
                    });
    }

    void printWatched(int mode){
        ListView films;
        films=findViewById(R.id.ListFilms);
        listAdapter = new ProfileListView(this,filmsArray,mode);
        films.setAdapter(listAdapter);
    }

    private void sortMoviesUsingDateAndTime(ArrayList<ProfileFilmListItem> toSort) {
        toSort.sort(new Comparator<ProfileFilmListItem>() {
            @Override
            public int compare(ProfileFilmListItem o1, ProfileFilmListItem o2) {
                if (o1.getDate().compareTo(o2.getDate()) == 0) {
                    return o1.getTime().compareTo(o2.getTime());
                } else {
                    return o1.getDate().compareTo(o2.getDate());
                }
            }
        });
    }

    private void sortMoviesUsingDateDescending(ArrayList<ProfileFilmListItem> toSort) {
        toSort.sort(new Comparator<ProfileFilmListItem>() {
            @Override
            public int compare(ProfileFilmListItem o1, ProfileFilmListItem o2) {
                return (o1.getDate().compareTo(o2.getDate())) * -1;
            }
        });
    }
}
