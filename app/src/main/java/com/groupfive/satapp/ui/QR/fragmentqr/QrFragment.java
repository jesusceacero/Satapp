package com.groupfive.satapp.ui.QR.fragmentqr;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.groupfive.satapp.R;
import com.groupfive.satapp.ui.QR.activity.QRActivity;


public class QrFragment extends Fragment {
    private View view;
    private Button qr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qr, container, false);
        Intent i = new Intent(getActivity(), QRActivity.class);
        startActivity(i);

        qr = view.findViewById(R.id.buttonQr);

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), QRActivity.class);
                startActivity(i);
            }
        });





        return view;
    }
}
