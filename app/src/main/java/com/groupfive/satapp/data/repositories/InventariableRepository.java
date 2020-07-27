package com.groupfive.satapp.data.repositories;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.snackbar.Snackbar;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.retrofit.service.SatAppInvService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventariableRepository {

    SatAppInvService service;
    LiveData<List<Inventariable>> data;

    public InventariableRepository() {
        service = SatAppServiceGenerator.createService(SatAppInvService.class);
    }

    public LiveData<List<Inventariable>> getAllInventariables() {
        final MutableLiveData<List<Inventariable>> data = new MutableLiveData<>();

        Call<List<Inventariable>> call = service.getInventariables();

        call.enqueue(new Callback<List<Inventariable>>() {
            @Override
            public void onResponse(Call<List<Inventariable>> call, Response<List<Inventariable>> response) {
                if(response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Toast.makeText(MyApp.getContext(), "API Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Inventariable>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "External error", Toast.LENGTH_LONG).show();
            }
        });
        return data;
    }

    public LiveData<Inventariable> getInventariable(String id) {
        final MutableLiveData<Inventariable> data = new MutableLiveData<>();

        Call<Inventariable> call = service.getInventariable(id);

        call.enqueue(new Callback<Inventariable>() {
            @Override
            public void onResponse(Call<Inventariable> call, Response<Inventariable> response) {
                if(response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Toast.makeText(MyApp.getContext(), "API Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Inventariable> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
        return data;
    }

    public LiveData<List<TicketModel>> getTicketsFrom(String id) {
        final MutableLiveData<List<TicketModel>> data = new MutableLiveData<>();

        Call<List<TicketModel>> call = service.getTicketsFromInventariable(id);

        call.enqueue(new Callback<List<TicketModel>>() {
            @Override
            public void onResponse(Call<List<TicketModel>> call, Response<List<TicketModel>> response) {
                if(response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Toast.makeText(MyApp.getContext(), "API Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TicketModel>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        });

        return data;
    }

    public LiveData<List<String>> getAllLocations() {
        final MutableLiveData<List<String>> data2 = new MutableLiveData<>();

        Call<List<String>> call = service.getLocations();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.isSuccessful()) {
                    data2.setValue(response.body());
                } else {
                    Toast.makeText(MyApp.getContext(), "API Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "External error", Toast.LENGTH_LONG).show();
            }
        });
        return data2;
    }

}
