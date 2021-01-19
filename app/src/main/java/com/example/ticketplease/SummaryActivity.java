package com.example.ticketplease;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Random;

public class SummaryActivity extends AppCompatActivity {
    public final static String TAG = "SummaryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_page);
        createNotificationChannel();

        String numberOfTickets = getIntent().getStringExtra("Tickets");
        String dateTicket = getIntent().getStringExtra("Date");
        String timeOnTicket = getIntent().getStringExtra("Time");
        String title = getIntent().getStringExtra("Title");

        TextView text = findViewById(R.id.NumberOfTickets);

        if(numberOfTickets.equals("2")||numberOfTickets.equals("3")||numberOfTickets.equals("4")) {
            text.setText("Kupiłeś "+ numberOfTickets + " bilety");
        } else if(numberOfTickets.equals("1")){
            text.setText("Kupiłeś 1 bilet");
        }else  {
            text.setText("Kupiłeś "+ numberOfTickets +" biletów");
        }
        TextView textWithDate=findViewById(R.id.date);
        textWithDate.setText("Data seansu: "+dateTicket);
        Button ok;
        ok = findViewById(R.id.chip6);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SummaryActivity.this,HomeActivity.class));
            }
        });


        setNotification(setCalendar(dateTicket, timeOnTicket), title, timeOnTicket);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SummaryActivity.this,HomeActivity.class));
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence title = "Movie Title placeholder";
            String description = "Masz seans za 2 godziny. Ruszaj dupe xD";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("reminder", title, importance);
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
            Random rand=new Random();
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

        Log.d(TAG, String.valueOf(cal.getTimeInMillis()));
        Log.d(TAG, String.valueOf(Integer.parseInt(splitedDate[0])));
        Log.d(TAG, String.valueOf(Integer.parseInt(splitedDate[1]) - 1));
        Log.d(TAG, splitedDate[2]);
        Log.d(TAG, String.valueOf(Integer.parseInt(splitedTime[0]) - 2));
        Log.d(TAG, splitedTime[1]);
        Log.d(TAG, String.valueOf(cal.getTime()));
        Log.d(TAG, String.valueOf(cal.get(Calendar.MONTH)));

        return cal;
    }
}
