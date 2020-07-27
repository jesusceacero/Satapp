package com.groupfive.satapp.ui.annotations.allticketannotation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.groupfive.satapp.R;
import com.groupfive.satapp.listeners.ITicketAnnotationListener;
import com.groupfive.satapp.models.tickets.TicketAnotaciones;

public class ShowAllTicketAnnotationsActivity extends AppCompatActivity implements ITicketAnnotationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_ticket_annotations);
    }

    @Override
    public void onFotoItemClick(TicketAnotaciones ticketAnotaciones) {

    }
}
