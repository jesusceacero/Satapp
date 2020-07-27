package com.groupfive.satapp.data.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.groupfive.satapp.data.repositories.InventariableRepository;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.models.tickets.TicketModel;

import java.util.List;

public class InventariableViewModel extends AndroidViewModel {
    InventariableRepository inventariableRepository;
    Context ctx;
    LiveData<List<Inventariable>> direct;
    LiveData<Inventariable> result;
    LiveData<List<TicketModel>> result2;
    LiveData<List<String>> result3;

    public InventariableViewModel(@NonNull Application application) {
        super(application);
        this.inventariableRepository = new InventariableRepository();
        this.ctx = application.getApplicationContext();
    }

    public LiveData<List<Inventariable>> getAllInventariables() {
        direct = inventariableRepository.getAllInventariables();
        return direct;
    }

    public LiveData<Inventariable> getInventariable(String id) {
        result = inventariableRepository.getInventariable(id);
        return result;
    }

    public LiveData<List<TicketModel>> getTicketFrom(String id) {
        result2 = inventariableRepository.getTicketsFrom(id);
        return result2;
    }

    public LiveData<List<String>> getLocations() {
        result3 = inventariableRepository.getAllLocations();
        return result3;
    }
}
