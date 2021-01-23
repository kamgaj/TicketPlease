package com.example.ticketplease;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileCustomListView {
    private TextView title ;
    private ImageView poster;
    private TextView description;

    public ProfileCustomListView(TextView title, ImageView poster, TextView description) {
        this.title = title;
        this.poster = poster;
        this.description = description;
    }

    public TextView getTitle() {
        return title;
    }

    public ImageView getPoster() {
        return poster;
    }

    public TextView getDescription() {
        return description;
    }
}
