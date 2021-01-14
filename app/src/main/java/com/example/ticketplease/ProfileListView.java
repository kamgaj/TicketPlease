package com.example.ticketplease;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileListView extends BaseAdapter {
    public ArrayList<ProfileFilmListItem> listFilms;
    private final Context context;

    public ProfileListView(Context context,ArrayList<ProfileFilmListItem> listFilms) {
        this.context = context;
        this.listFilms = listFilms;
    }

    @Override
    public int getCount() {
        return listFilms.size();
    }

    @Override
    public ProfileFilmListItem getItem(int position) {
        return listFilms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ProfileCustomListView listViewHolder;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.profilefilmlist,parent,false);
            listViewHolder = new ProfileCustomListView();
            listViewHolder.title = row.findViewById(R.id.FilmTitleWatched);
            listViewHolder.description = row.findViewById(R.id.DescriptionText);
            listViewHolder.poster = row.findViewById(R.id.posterPlace);
            row.setTag(listViewHolder);
        }
        else
        {
            row=convertView;
            listViewHolder= (ProfileCustomListView) row.getTag();
        }
        final ProfileFilmListItem products = getItem(position);

        listViewHolder.title.setText(products.Title);
        Picasso.get().load(products.Poster).into(listViewHolder.poster);
        listViewHolder.description.setText(products.Description);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DescriptionActivity.class);
                intent.putExtra("Movie_title", products.Title);
                context.startActivity(intent);
            }
        });
        return row;
    }
}
