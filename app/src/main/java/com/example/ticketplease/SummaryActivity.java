package com.example.ticketplease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_page);
        String numberOfTickets = getIntent().getStringExtra("Tickets");
        TextView text = findViewById(R.id.NumberOfTickets);

        if(numberOfTickets.equals("0")) {
            text.setText("Kupiłeś 0 biletów");
        } else if(numberOfTickets.equals("1")){
            text.setText("Kupiłeś 1 bilet");
        }else  {
            text.setText("Kupiłeś "+ numberOfTickets + " bilety");
        }

        Button ok;
        ok = findViewById(R.id.chip6);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SummaryActivity.this,HomeActivity.class));
            }
        });
    }
}
