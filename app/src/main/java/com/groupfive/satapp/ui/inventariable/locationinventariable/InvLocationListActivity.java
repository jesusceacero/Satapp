package com.groupfive.satapp.ui.inventariable.locationinventariable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.groupfive.satapp.R;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.listeners.IInvLocationListener;

public class InvLocationListActivity extends AppCompatActivity implements IInvLocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_location_list);
    }

    @Override
    public void OnInvLocationClick(Inventariable inv) {

    }
}
