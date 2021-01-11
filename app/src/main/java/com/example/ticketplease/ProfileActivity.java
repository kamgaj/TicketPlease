package com.example.ticketplease;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
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
    private FirebaseFirestore db= FirebaseFirestore.getInstance();

    ArrayList<ProfileFilmListItem> filmsArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        TextView logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

            }
        });

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        uID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();

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

        getTopRatedFilms();
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
                getNewestFilms();
            }
        });
        RadioButton Booked = (RadioButton) findViewById(R.id.booked);
        Booked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsArray.clear();
                getTopRatedFilms();
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
    private void getNewestFilms() {
        storageReference = FirebaseStorage.getInstance().getReference();

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
                                filmsArray.add(new ProfileFilmListItem(document.getString("Title"), document.getString("Description"), path));
                                Log.d("Zdjecie",path);
                            }
                            PrintWatched();
                        } else {
                            Log.d("ProfilePage", "Error getting documents: ", task.getException());
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
                                filmsArray.add(new ProfileFilmListItem(document.getString("Title"), document.getString("Description"), path));
                            }
                            PrintWatched();
                        } else {
                            Log.d("ProfilePage2", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    void PrintWatched(){
        ListView films;
        ListAdapter listAdapter;
        films=findViewById(R.id.ListFilms);
        listAdapter = new ProfileListView(this,filmsArray);
        films.setAdapter(listAdapter);
    }

//    public void logout(View view){
//        FirebaseAuth.getInstance().signOut();
//        Intent logoutIntent = new Intent(ProfileActivity.this,LoginActivity.class);
//        logoutIntent.putExtra("logoutCode", 2137);
//        startActivity(logoutIntent);
//    }
}
