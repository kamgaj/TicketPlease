package com.example.ticketplease;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private String uID;
    private ImageView profilePicture;

    ArrayList<ProfileFilmListItem> filmsArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

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
        /*ListView films;
        String[] Titles;
        films=findViewById(R.id.ListFilms);
        Titles = new DateFormatSymbols().getMonths();
        ArrayAdapter<String> titlesArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,Titles);
        films.setAdapter(titlesArray);*/
        getFilms();
        ListView films;
        ListAdapter listAdapter;
        films=findViewById(R.id.ListFilms);
        listAdapter = new ProfileListView(this,filmsArray);
        films.setAdapter(listAdapter);

        //Tu zaczyna sie moja porazka xD
        profilePicture = findViewById(R.id.ProfilePicture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGaleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGaleryIntent, 2137);
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

    private void getFilms() {
        filmsArray.add(new ProfileFilmListItem("Czarny ekran","Film bez wizji",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 2","Film bez wizji. Kolejna część oscarowej produkcji",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 3","Film bez wizji. Niektórzy myślą, że to cały czas pierwsza część",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 4","Film bez wizji. Niektórzy myślą, że to cały czas pierwsza część",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 5","Film bez wizji. Niektórzy myślą, że to cały czas pierwsza część",R.drawable.small_poster));
        filmsArray.add(new ProfileFilmListItem("Czarny ekran 6","Film bez wizji. Niektórzy myślą, że to cały czas pierwsza część",R.drawable.small_poster));
    }
    public void goToDescription(View view) {
        startActivity(new Intent(ProfileActivity.this, DescriptionActivity.class));
    }
    public void logout(View view){
        startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
    }
}
