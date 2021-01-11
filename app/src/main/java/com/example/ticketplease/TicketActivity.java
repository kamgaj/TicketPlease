package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class TicketActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket);
        List<Integer> test=new ArrayList<Integer>();
        test.add(1);
        test.add(2);
        test.add(3);
        generateQRPage(1,"Test","13.02.2020",3,"Cinema",test);
        //addNoTicketMessage();
        /*try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
           ;
        }*/
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
    void generateQRPage(int id,String title, String date, int nrOfTickets, String cinema, List<Integer> seats){
        ConstraintLayout constraintLayout=findViewById(R.id.qrPlace);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        constraintLayout.removeAllViews();
        View view = layoutInflater.inflate(R.layout.qr_page, constraintLayout, false);
        ImageView qrCode = view.findViewById(R.id.qrCode);
        TextView movieTitle = view.findViewById(R.id.movieTitle);
        TextView movieDate = view.findViewById(R.id.date);
        TextView nrOFTickets  = view.findViewById(R.id.numberOfTickets);
        TextView Cinema = view.findViewById(R.id.cinemaName);
        TextView Seats = view.findViewById(R.id.seatsIDs);
        JSONObject jsonObject=new JSONObject();
        movieTitle.setText(title);
        movieDate.setText(date);
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
        constraintLayout.addView(view);
    }
    void addNoTicketMessage(){
        ConstraintLayout constraintLayout=findViewById(R.id.qrPlace);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        constraintLayout.removeAllViews();
        View view = layoutInflater.inflate(R.layout.no_qr_page, constraintLayout, false);
        constraintLayout.addView(view);
    }
}
