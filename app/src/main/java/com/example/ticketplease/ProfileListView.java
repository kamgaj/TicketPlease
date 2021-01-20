package com.example.ticketplease;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ProfileListView extends BaseAdapter {
    public ArrayList<ProfileFilmListItem> listFilms;
    private final Context context;
    private int mode;

    public ProfileListView(Context context,ArrayList<ProfileFilmListItem> listFilms,int mode) {
        this.context = context;
        this.listFilms = listFilms;
        this.mode=mode;
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
        if(mode==0){
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DescriptionActivity.class);
                    intent.putExtra("Movie_title", products.Title);
                    context.startActivity(intent);
                }
            });

        }else {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog qr;
                    qr=new Dialog(context);
                    qr.setContentView(R.layout.qr_profile);
                    qr.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    JSONObject jsonObject=new JSONObject();
                    TextView time;
                    TextView date;
                    TextView numberOfTickets;
                    TextView cinema;
                    TextView seats;
                    ImageView qrCode=qr.findViewById(R.id.qrCodeInProfile);
                    time=qr.findViewById(R.id.TimeTextQR);
                    date=qr.findViewById(R.id.date);
                    numberOfTickets=qr.findViewById(R.id.numberOfTickets);
                    cinema=qr.findViewById(R.id.cinemaName);
                    seats=qr.findViewById(R.id.seatsIDs);
                    Chip ok;
                    ok=qr.findViewById(R.id.okButtonQR);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            qr.dismiss();
                        }
                    });
                    try {
                        jsonObject.put("ID",products.Id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    QRGEncoder qrgEncoder = new QRGEncoder(jsonObject.toString(), null, QRGContents.Type.TEXT,900);;
                    try {
                        qrCode.setImageBitmap(qrgEncoder.encodeAsBitmap());
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    time.setText(products.Time);
                    date.setText(products.Date);
                    numberOfTickets.setText(products.numberTickets);
                    cinema.setText(products.Cinema);
                    seats.setText(products.Seats);
                    qr.show();
                }
            });
        }
        return row;
    }
}
