package com.example.ticketplease;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Random;

public class SummaryActivity extends AppCompatActivity {
    String numberOfTickets;
    String dateTicket;
    String timeOnTicket;
    String title;
    Boolean areNotificationsEnabled;
    Button ok;
    Calendar cal;
    TextView text;
    TextView textWithDate;
    TextView textView;
    Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_page);
        createNotificationChannel();
        getFromIntent();

        rand = new Random();
        cal = setCalendar(dateTicket, timeOnTicket);
        text = findViewById(R.id.NumberOfTickets);
        textWithDate = findViewById(R.id.date);
        ok = findViewById(R.id.chip6);
        textView = findViewById(R.id.numberOfTickets);
        areNotificationsEnabled = checkIfNotificationCanBeSent();
        setAllTextViews();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SummaryActivity.this,HomeActivity.class));
            }
        });
        if(Boolean.TRUE.equals(areNotificationsEnabled)) {
            setNotification(setCalendar(dateTicket, timeOnTicket), title, timeOnTicket);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SummaryActivity.this,HomeActivity.class));
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence titlePlaceholder = "Movie Title placeholder";
            String description = "Description placeholder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("reminder", titlePlaceholder, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setNotification(Calendar cal, String title, String timeOnTicket) {
        if(cal.getTimeInMillis()+3600>=Calendar.getInstance().getTimeInMillis()) {
            Intent intent = new Intent(SummaryActivity.this, ReminderBroadcast.class);
            intent.putExtra("Title", title);
            intent.putExtra("Time", timeOnTicket);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(SummaryActivity.this, rand.nextInt(10000), intent, 0);


            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            Toast.makeText(getApplicationContext(), "Przypomnienie ustawione", Toast.LENGTH_SHORT).show();
        }

    }

    private Calendar setCalendar(String date, String time) {
        String[] splitedDate = date.split("\\.");
        String[] splitedTime = time.split(":");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splitedDate[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(splitedDate[1]) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(splitedDate[2]));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitedTime[0]) - 2);
        cal.set(Calendar.MINUTE, Integer.parseInt(splitedTime[1]));
        cal.set(Calendar.SECOND, 0);

        return cal;
    }

    private Boolean checkIfNotificationCanBeSent() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("NotificationPermission", true);
    }

    private void getFromIntent() {
        numberOfTickets = getIntent().getStringExtra("Tickets");
        dateTicket = getIntent().getStringExtra("Date");
        timeOnTicket = getIntent().getStringExtra("Time");
        title = getIntent().getStringExtra("Title");
    }

    private void setAllTextViews() {
        Resources res = getResources();
        if(numberOfTickets.equals("2")||numberOfTickets.equals("3")||numberOfTickets.equals("4")) {
            text.setText(String.format(res.getString(R.string.booked234Tickets), numberOfTickets));
        } else if(numberOfTickets.equals("1")){
            text.setText(R.string.booked1Ticket);
        }else  {
            text.setText(String.format(res.getString(R.string.bookedManyTickets), numberOfTickets));
        }
        String dateText = String.format(res.getString(R.string.movieDate), dateTicket);
        textWithDate.setText(dateText);
        if(cal.getTimeInMillis() + 3600 >= Calendar.getInstance().getTimeInMillis()) {
            textView.setText(R.string.notification_2_hour);
        }
        else{
            textView.setText("");
        }
    }
}
