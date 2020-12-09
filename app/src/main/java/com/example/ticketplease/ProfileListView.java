package com.example.ticketplease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class ProfileListView extends BaseAdapter {
    public ArrayList<ProfileFilmListItem> listFilms;
    private Context context;

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
        listViewHolder.poster.setImageResource(products.Poster);
        listViewHolder.description.setText(products.Description);


        return row;
    }
}
