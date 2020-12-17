package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BookingActivity  extends AppCompatActivity {
    ArrayList<Integer> alreadyBooked= new ArrayList<>();
    ArrayList<String> userChoice = new ArrayList<String>();
    int tickets=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page);
        ImageView buttonNext;
        buttonNext = (ImageView) findViewById(R.id.imageView5);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), String.valueOf(tickets)+"  i   "+userChoice.toString(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(BookingActivity.this, SummaryActivity.class);
                intent.putExtra("Tickets", String.valueOf(tickets));
                startActivity(intent);
            }
        });
        AddBookedBefore();
        addButtons();

    }
    public void descriptionFromBooking(View view){
        startActivity(new Intent(BookingActivity.this,DescriptionActivity.class));
    }
    void addButtons(){
        LinearLayout linearLayout;
        linearLayout = findViewById(R.id.seatPleace);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        ArrayList<Button> buttons = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for(int j=0;j<7;j++){
                View view = layoutInflater.inflate(R.layout.booking_button, linearLayout, false);
                Button button = view.findViewById(R.id.buttonBooking);
                button.setBackgroundColor(getResources().getColor(R.color.white));
                button.setTag("0");
                buttons.add(button);
                int finalJ = j;
                int finalI = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(button.getTag()=="1"){
                            button.setBackgroundColor(getResources().getColor(R.color.white));
                            button.setTag("0");
                            userChoice.remove(String.valueOf(7*finalI + finalJ));
                            tickets--;

                        }else {
                            button.setBackgroundColor(getResources().getColor(R.color.black));
                            button.setTag("1");
                            userChoice.add(String.valueOf(7*finalI + finalJ));
                            tickets++;
                        }
                    }
                });

                row.addView(view);
            }

            linearLayout.addView(row);
        }
        for(int i=0;i<alreadyBooked.size();i++){
            buttons.get(alreadyBooked.get(i)).setEnabled(false);
            buttons.get(alreadyBooked.get(i)).setBackgroundColor(getResources().getColor(R.color.mainColor));
        }
    }
    void AddBookedBefore(){
        alreadyBooked.add(5);
        alreadyBooked.add(6);
        alreadyBooked.add(20);
    }
}
