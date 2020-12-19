package com.example.ticketplease;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DescriptionActivity extends AppCompatActivity {

    int x=0;
    StorageReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);
        TextView title = findViewById(R.id.titleText);
        String titleFromIntent = getIntent().getStringExtra("Movie_title");
        x = getIntent().getIntExtra("From_where",0);
        title.setText(titleFromIntent);
        TextView description = findViewById(R.id.descriptionText);
        TextView genres = findViewById(R.id.genreText);
        ImageView moviePoster = findViewById(R.id.filmPoster);

        getMovieFromFirebase(titleFromIntent, description, genres, moviePoster);

        ImageView BuyTicket;
        BuyTicket = (ImageView) findViewById(R.id.BuyTicketButton);
        BuyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DescriptionActivity.this,BookingActivity.class));
            }
        });
    }
    public void returnToPreviousScreen(View view) {
        finish();
    }

    private void getMovieFromFirebase(String movieTitle, TextView tvDesc, TextView genres, ImageView poster) {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        List<String> genresList = new ArrayList<>();

        db.collection("Movies")
                .whereEqualTo("Title", movieTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                tvDesc.setText(document.getString("Description"));
                                genresList.addAll((Collection<? extends String>) document.get("Genres"));

                                String path = document.getString("Poster_link");
                                FirebaseStorage storage = FirebaseStorage.getInstance();

                                ref = storage.getReferenceFromUrl(path);
                                ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        poster.setImageBitmap(bitmap);
                                    }
                                });

                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for(String str : genresList) {
                                stringBuilder.append(str).append(", ");
                            }
                            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1); //remove last comma
                            genres.setText(stringBuilder.toString());
                        }
                    }
                });
    }

}
