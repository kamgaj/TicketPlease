package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DescriptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);
        TextView title = findViewById(R.id.titleText);
        String titleFromIntent = getIntent().getStringExtra("Movie_title");
        title.setText(titleFromIntent);
        TextView description = findViewById(R.id.descriptionText);
        TextView genres = findViewById(R.id.genreText);

        getEntryFromFirebase(titleFromIntent, description, genres);

        ImageView BuyTicket;
        BuyTicket = (ImageView) findViewById(R.id.BuyTicketButton);
        BuyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DescriptionActivity.this,BookingActivity.class));
            }
        });
    }
    public void goToSearch(View view) {
        startActivity(new Intent(DescriptionActivity.this, SearchActivity.class));
    }

    private void getEntryFromFirebase(String movieTitle, TextView tvDesc, TextView genres) {
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
