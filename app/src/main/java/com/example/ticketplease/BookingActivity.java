package com.example.ticketplease;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingActivity  extends AppCompatActivity {
    ArrayList<Integer> alreadyBooked= new ArrayList<>();
    ArrayList<String> userChoice = new ArrayList<String>();
    List<String> cinema=new ArrayList<>();
    List<String> technology=new ArrayList<>();
    Calendar calendar=Calendar.getInstance();
    String readyDate=String.valueOf(calendar.get(calendar.DAY_OF_MONTH))+"."+String.valueOf(calendar.get(calendar.MONTH)+1)+"."+String.valueOf(calendar.get(calendar.YEAR));
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
                //Toast.makeText(getApplicationContext(), String.valueOf(tickets)+"  i   "+userChoice.toString(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(BookingActivity.this, SummaryActivity.class);
                intent.putExtra("Tickets", String.valueOf(tickets));
                intent.putExtra("Date", readyDate);
                startActivity(intent);
            }
        });
        AddBookedBefore();
        addButtons();
        Chip date = findViewById(R.id.dateFilm);
        date.setHint(readyDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog=new DatePickerDialog(BookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i,i1,i2);
                        readyDate = String.valueOf(i2)+"."+String.valueOf(i1+1)+"."+String.valueOf(i);
                        date.setHint(readyDate);
                    }
                }, day,month,year);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.updateDate(year,month,day);

                datePickerDialog.show();
            }
        });
        addCinema();
        Chip Cinema = findViewById(R.id.cinema);
        Cinema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] t=cinema.toArray(new String[0]);
                AlertDialog.Builder builder=new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle("Wybierz kino");
                builder.setSingleChoiceItems(t, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Cinema.setHint(t[i]);
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
        addTechnology();
        Chip Tech = findViewById(R.id.technology);
        Tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] t=technology.toArray(new String[0]);
                AlertDialog.Builder builder=new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle("Wybierz technologie");
                builder.setSingleChoiceItems(t, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Tech.setHint(t[i]);
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

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
    void addCinema(){
        cinema.add("Kino 1");
        cinema.add("Kino 2");
        cinema.add("Kino 3");
        cinema.add("Kino 4");
    }
    void addTechnology(){
        technology.add("2D");
        technology.add("3D");
        technology.add("4D");
        technology.add("5D");
    }
}
