package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class TicketActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket);
        mFirebaseAuth = FirebaseAuth.getInstance();
        if(mFirebaseAuth.getCurrentUser()==null){
            Intent goToLogin = new Intent(TicketActivity.this, LoginActivity.class);
            goToLogin.putExtra("disableBackButton", 2137);
            startActivity(goToLogin);
        }
        addQRfromFirebase();

        ImageView Profile;
        Profile = (ImageView) findViewById(R.id.profileButton);
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TicketActivity.this, ProfileActivity.class));
            }
        });
        ImageView Search;
        Search = (ImageView) findViewById(R.id.searchButton);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TicketActivity.this, SearchActivity.class));
            }
        });
        ImageView Home;
        Home = (ImageView) findViewById(R.id.homeButton);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TicketActivity.this, HomeActivity.class));
            }
        });

    }
    void clear(){
        LinearLayout linearLayout=findViewById(R.id.qrPlace);
        linearLayout.removeAllViews();
    }
    void generateQRPage(String id,String title, String date, String time, int nrOfTickets, String cinema, List<Integer> seats){
        LinearLayout linearLayout=findViewById(R.id.qrPlace);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.qr_page, linearLayout, false);
        ImageView qrCode = view.findViewById(R.id.qrCode);
        TextView movieTitle = view.findViewById(R.id.movieTitle);
        TextView movieDate = view.findViewById(R.id.date);
        TextView movieTime = view.findViewById(R.id.TimeTextQR);
        TextView nrOFTickets  = view.findViewById(R.id.numberOfTickets);
        TextView Cinema = view.findViewById(R.id.cinemaName);
        TextView Seats = view.findViewById(R.id.seatsIDs);
        JSONObject jsonObject=new JSONObject();
        movieTitle.setText(title);
        movieDate.setText(date);
        movieTime.setText(time);
        Cinema.setText(cinema);
        String temp="";
        for(int i=0;i<seats.size();i++){
            temp+=seats.get(i);
            if(i!=seats.size()-1){
                temp+=", ";
            }
        }
        Seats.setText(temp);
        nrOFTickets.setText("" + nrOfTickets);
        try {
            jsonObject.put("ID",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        QRGEncoder qrgEncoder = new QRGEncoder(jsonObject.toString(), null, QRGContents.Type.TEXT,1256);;
        try {
            qrCode.setImageBitmap(qrgEncoder.encodeAsBitmap());
        } catch (WriterException e) {
            e.printStackTrace();
        }
        linearLayout.addView(view);
    }
    void addNoTicketMessage(){
        LinearLayout linearLayout=findViewById(R.id.qrPlace);
        linearLayout.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.no_qr_page, linearLayout, false);
        linearLayout.addView(view);
    }
    void addQRfromFirebase(){
        clear();
        db.collection("Bookings")
                .whereEqualTo("userID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            int counter=0;
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String date = document.getString("date");
                                String title = document.getString("movieName");
                                String id = document.getId();
                                List<Integer> seats=new ArrayList<>();
                                seats.add(5);
                                generateQRPage(id,title,date,"",0,"",seats);
                                counter++;
                            }

                            if(counter==0){
                                addNoTicketMessage();
                            }
                        }
                    }
                });
    }
}
