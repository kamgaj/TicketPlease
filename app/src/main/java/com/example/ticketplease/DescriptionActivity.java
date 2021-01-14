package com.example.ticketplease;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DescriptionActivity extends AppCompatActivity {
    TextView title;
    TextView description;
    TextView genres;
    TextView duration;
    TextView release_date;
    TextView rating;
    boolean ready=false;
    int x=0;
    StorageReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);
        title = findViewById(R.id.titleText);
        String titleFromIntent = getIntent().getStringExtra("Movie_title");
        x = getIntent().getIntExtra("From_where",0);
        title.setText(titleFromIntent);
        description = findViewById(R.id.descriptionText);
        genres = findViewById(R.id.genreText);
        duration = findViewById(R.id.duration);
        release_date = findViewById(R.id.release_date);
        rating = findViewById(R.id.rating);
        ImageView moviePoster = findViewById(R.id.filmPoster);


        getMovieFromFirebase(titleFromIntent, description, genres, moviePoster, duration, release_date, rating);


        ImageView BuyTicket;
        BuyTicket = (ImageView) findViewById(R.id.BuyTicketButton);
        BuyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ready){
                    Intent intent=new Intent(DescriptionActivity.this,BookingActivity.class);
                    intent.putExtra("movieTitle", getIntent().getStringExtra("Movie_title"));
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Plakat nie został w pełni załadowany", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    public void returnToPreviousScreen(View view) {
        finish();
    }

    private void getMovieFromFirebase(String movieTitle, TextView tvDesc, TextView genres, ImageView poster, TextView duration, TextView release_date, TextView rating) {
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

                                String dur = "Czas trwania: " + document.getString("Duration");
                                duration.setText(dur);

                                String release = "Data premiery: " + document.getString("Release_date");
                                release_date.setText(release);

                                String rat = "Ocena: " + document.get("Rating");
                                rating.setText(rat);

                                makeTextViewResizable(tvDesc, 4, "Zobacz więcej", true);
                                genresList.addAll((Collection<? extends String>) document.get("Genres"));

                                String path = document.getString("Poster_link");
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                ref = storage.getReferenceFromUrl(path);
                                ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        createImageFromBitmap(bitmap);
                                        poster.setImageBitmap(bitmap);
                                        ready=true;
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

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                ((ViewTreeObserver) obs).removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "Zobacz mniej", false);
                    } else {
                        makeTextViewResizable(tv, 4, "Zobacz więcej", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }
    public void createImageFromBitmap(Bitmap bitmap) {
        String fileName = "Poster";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
