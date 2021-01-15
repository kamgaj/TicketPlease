package com.example.ticketplease;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


import jp.wasabeef.blurry.Blurry;

public class BookingActivity  extends AppCompatActivity {
    ArrayList<Integer> alreadyBooked= new ArrayList<>();
    ArrayList<String> userChoice = new ArrayList<String>();
    List<String> cinema=new ArrayList<>();
    List<String> technology=new ArrayList<>();
    List<String> Time=new ArrayList<>();
    Calendar calendar=Calendar.getInstance();
    String readyDate=String.valueOf(calendar.get(calendar.DAY_OF_MONTH))+"."+String.valueOf(calendar.get(calendar.MONTH)+1)+"."+String.valueOf(calendar.get(calendar.YEAR));
    int tickets=0;
    private BookingInfo bookingInfo;
    List<Integer> seatNumbers = new ArrayList<>();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page);

        db = FirebaseFirestore.getInstance();
        bookingInfo = new BookingInfo();
        bookingInfo.setDate(readyDate);
        bookingInfo.setUserID(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        String titleFromIntent = getIntent().getStringExtra("movieTitle");
        bookingInfo.setMovieName(titleFromIntent);

        Toolbar buttonNext = (Toolbar) findViewById(R.id.toolbarUPDown);
        TextView CinemaText=findViewById(R.id.CinemaText);
        TextView time=findViewById(R.id.TimeMovieText);
        TextView techText=findViewById(R.id.TechnologyText);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CinemaText.getText().toString().trim().equals("Kino")){
                    Toast.makeText(getApplicationContext(), "Aby kontynuować, wybierz kino", Toast.LENGTH_LONG).show();
                }
                else if(techText.getText().toString().trim().equals("Technologia")){
                    Toast.makeText(getApplicationContext(), "Aby kontynuować, wybierz technologię", Toast.LENGTH_LONG).show();
                }
                else if(time.getText().toString().trim().equals("Godzina")){
                    Toast.makeText(getApplicationContext(), "Aby kontynuować, wybierz godzinę", Toast.LENGTH_LONG).show();
                }
                else if(tickets==0){
                    Toast.makeText(getApplicationContext(), "Aby kontynuować, wybierz miejsce", Toast.LENGTH_LONG).show();
                }else if(calendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)&&calendar.get(Calendar
               .MONTH)==Calendar.getInstance().get(Calendar.MONTH)&&calendar.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)
                        &LocalTime.now().plusMinutes(30).compareTo(LocalTime.parse(time.getText().toString() + ":00"))>0) {
                    Toast.makeText(getApplicationContext(), "Do seansu pozostało mniej niż 30 minut. Dokonanie rezerwacji jest niemożliwe", Toast.LENGTH_LONG).show();
                } else {
                bookingInfo.setSeats(seatNumbers);
                pushBookingToBase(bookingInfo);

                Intent intent = new Intent(BookingActivity.this, SummaryActivity.class);
                intent.putExtra("Tickets", String.valueOf(tickets));
                intent.putExtra("Date", readyDate);
                startActivity(intent);
                }
            }
        });

        addCinema();
        addTime();
        TextView dateText=findViewById(R.id.dateMovieText);
        Chip date = findViewById(R.id.dateFilm);
        dateText.setText(readyDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i, i1, i2);
                        readyDate = String.valueOf(i2) + "." + String.valueOf(i1 + 1) + "." + String.valueOf(i);
                        dateText.setText(readyDate);
                        bookingInfo.setDate(readyDate);
                        if(!CinemaText.getText().toString().trim().equals("Kino") && !techText.getText().toString().trim().equals("Technologia") && !time.getText().toString().trim().equals("Godzina")){
                            clearBookedSeats();
                            addButtons();
                        }
                    }
                }, day, month, year);
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.updateDate(year, month, day);

                datePickerDialog.show();
            }
        });
        Chip timeChip=findViewById(R.id.TimeFilm);
        timeChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String[] t;
                List<String> temp=new ArrayList<>();
                Calendar T=Calendar.getInstance();
               if(calendar.get(Calendar.DAY_OF_MONTH) == T.get(Calendar.DAY_OF_MONTH)&&calendar.get(Calendar
               .MONTH)==T.get(Calendar.MONTH)&&calendar.get(Calendar.YEAR)==T.get(Calendar.YEAR)){
                   LocalTime currentTime=LocalTime.now().plusMinutes(30);
                   for (int i=0;i<Time.size();i++){
                       CharSequence timeN=Time.get(i)+":00";
                       LocalTime timeNew=LocalTime.parse(timeN);
                       if (currentTime.compareTo(timeNew)<0){
                           temp.add(Time.get(i));
                       }
                   }
                   t=temp.toArray(new String[0]);
               }
                else {
                   t=Time.toArray(new String[0]);
               }

                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle("Wybierz godzinę seansu");
                builder.setSingleChoiceItems(t, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        time.setText(t[i]);
                        bookingInfo.setTime(t[i]);
                        dialogInterface.dismiss();
                        if(!CinemaText.getText().toString().trim().equals("Kino") && !techText.getText().toString().trim().equals("Technologia")){
                            clearBookedSeats();
                            addButtons();
                        }
                    }
                });
                builder.show();
            }
        });
        Chip Cinema = findViewById(R.id.cinema);
        Cinema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] t = cinema.toArray(new String[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle("Wybierz kino");
                builder.setSingleChoiceItems(t, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CinemaText.setText(t[i]);
                        bookingInfo.setCinemaName(t[i]);
                        dialogInterface.dismiss();
                        if(!techText.getText().toString().trim().equals("Technologia") && !time.getText().toString().trim().equals("Godzina")){
                            clearBookedSeats();
                            addButtons();
                        }
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
                String[] t = technology.toArray(new String[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle("Wybierz technologie");
                builder.setSingleChoiceItems(t, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        techText.setText(t[i]);
                        bookingInfo.setTechnology(t[i]);
                        dialogInterface.dismiss();
                        if(!CinemaText.getText().toString().trim().equals("Kino") && !time.getText().toString().trim().equals("Godzina")){
                            clearBookedSeats();
                            addButtons();
                        }
                    }
                });
                builder.show();
            }
        });
        Bitmap b = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ticket);
        try {
            b = BitmapFactory.decodeStream(this.openFileInput("Poster"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
        ImageView newImage = new ImageView(this);
        ConstraintLayout constraintLayout=findViewById(R.id.bookingLayout);



        Bitmap bitmap=(Bitmap.createScaledBitmap(b, 200, 100, false));
        Blurry.with(this).from(bitmap).into(newImage);
       constraintLayout.setBackground(newImage.getDrawable());
    }

    void addButtons(){
        LinearLayout linearLayout;
        linearLayout = findViewById(R.id.seatPleace);
        linearLayout.removeAllViewsInLayout();
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
                         }
                        else {
                            button.setBackgroundColor(getResources().getColor(R.color.mainColor));
                            button.setTag("1");
                            userChoice.add(String.valueOf(7*finalI + finalJ));
                            seatNumbers.add(7*finalI + finalJ);
                            tickets++;
                        }
                    }
                });

                row.addView(view);
            }

            linearLayout.addView(row);
        }
        fetchAllBookingsForCurrentMovie(buttons);
    }

    void addCinema(){
        cinema.add("Cinema City Manufaktura");
        cinema.add("Helios Sukcesja");
        cinema.add("Multikino");
        cinema.add("Wytwórnia");
    }
    void addTechnology(){
        technology.add("2D");
        technology.add("3D");
        technology.add("4D");
        technology.add("5D");
    }
    void addTime(){
        Time.add("12:15");
        Time.add("15:10");
        Time.add("17:25");
        Time.add("18:35");
        Time.add("20:45");
    }

    public void returnToPreviousScreen(View view) {
        finish();
    }

    void pushBookingToBase(Object value) {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("Bookings")
                .add(value)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("BookingActivity --------- ", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                });
    }


    void fetchAllBookingsForCurrentMovie(ArrayList<Button> buttons) {
        db.collection("Bookings")
                .whereEqualTo("movieName", bookingInfo.getMovieName())
                .whereEqualTo("cinemaName", bookingInfo.getCinemaName())
                .whereEqualTo("date", bookingInfo.getDate())
                .whereEqualTo("technology", bookingInfo.getTechnology())
                .whereEqualTo("time", bookingInfo.getTime())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d("BookingActivity --------- ", "Success");
                                List<Long> seats = (List<Long>) document.get("seats");
                                List<Integer> temp = Objects.requireNonNull(seats).stream()
                                        .map(Long::intValue)
                                        .collect(Collectors.toList());
                                alreadyBooked.addAll(temp);
                            }

                            for(int i = 0; i < alreadyBooked.size(); i++) {
                                buttons.get(alreadyBooked.get(i)).setEnabled(false);
                                buttons.get(alreadyBooked.get(i)).setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        } else {
                            Log.d("BookingActivity --------- ", "Failed to fetch bookings");
                        }
                    }
                });
    }
    void clearBookedSeats() {
        alreadyBooked = new ArrayList<>();
    }
}
