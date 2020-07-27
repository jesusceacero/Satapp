package com.groupfive.satapp.ui.tickets.phototicketdetail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.groupfive.satapp.R;
import com.groupfive.satapp.listeners.IPhotoTicketDetailListener;

public class ShowPhotosTicektActivity extends AppCompatActivity implements IPhotoTicketDetailListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos_ticekt);
    }

    @Override
    public void onFotoItemClick(String foto) {

    }
}
