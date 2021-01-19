package com.example.ticketplease;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("Title");
        String time = intent.getStringExtra("Time");
        RemoteViews remoteViews = new RemoteViews(BuildConfig.APPLICATION_ID,  R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.content_title, title);
        remoteViews.setTextViewText(R.id.content_text, "Twój seans zaczyna się o " + time);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder")
                .setCustomContentView(remoteViews)
                .setSmallIcon(R.drawable.buy_ticket)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Random random=new Random();
        notificationManager.notify(random.nextInt(10000), builder.build());
    }
}
