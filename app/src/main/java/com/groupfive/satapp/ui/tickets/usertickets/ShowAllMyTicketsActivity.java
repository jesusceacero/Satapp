package com.groupfive.satapp.ui.tickets.usertickets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.listeners.IMyTicketListener;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.ui.tickets.ticketdetail.TicketDetailActivity;

public class ShowAllMyTicketsActivity extends AppCompatActivity implements IMyTicketListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_my_tickets);
    }

    @Override
    public void onMyTicketItemClick(TicketModel ticketModel) {
        Intent i = new Intent(ShowAllMyTicketsActivity.this, TicketDetailActivity.class);
        i.putExtra(Constants.EXTRA_TICKET_ID, String.valueOf(ticketModel.getId()));
        startActivity(i);
    }
}
