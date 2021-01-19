package com.example.ticketplease;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews remoteViews = new RemoteViews(BuildConfig.APPLICATION_ID,  R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.content_title, "Movie Title");
        remoteViews.setTextViewText(R.id.content_text, "Masz seans za 2 godziny. Ruszaj dupe xD");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder")
                .setCustomContentView(remoteViews)
                .setSmallIcon(R.drawable.buy_ticket)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(13, builder.build());
    }
}
