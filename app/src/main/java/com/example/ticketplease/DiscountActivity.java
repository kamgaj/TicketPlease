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

import java.util.Objects;

public class DiscountActivity extends AppCompatActivity {


    StorageReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discount);
        TextView title = findViewById(R.id.titleText);
        String titleFromIntent = getIntent().getStringExtra("Discount_title");
        title.setText(titleFromIntent);
        TextView description = findViewById(R.id.descriptionText);
        ImageView moviePoster = findViewById(R.id.OfferPoster);

        getDiscountFromFirebase(titleFromIntent, description, moviePoster);


    }
    public void goToSearch(View view) {
        startActivity(new Intent(DiscountActivity.this, SearchActivity.class));
    }

    private void getDiscountFromFirebase(String discountTitle, TextView tvDesc, ImageView discountImage) {
        FirebaseFirestore db= FirebaseFirestore.getInstance();

        db.collection("Discounts")
                .whereEqualTo("Title", discountTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                tvDesc.setText(document.getString("Description"));

                                String path = document.getString("Image_link");
                                FirebaseStorage storage = FirebaseStorage.getInstance();

                                ref = storage.getReferenceFromUrl(path);
                                ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        discountImage.setImageBitmap(bitmap);
                                    }
                                });

                            }

                        }
                    }
                });
    }
}
