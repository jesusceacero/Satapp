package com.groupfive.satapp.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.groupfive.satapp.data.repositories.SatAppRepository;
import com.groupfive.satapp.models.tickets.TicketModel;

import java.util.List;

public class AllMyTicketsViewModel extends AndroidViewModel {

    private SatAppRepository satAppRepository;
    private MutableLiveData<List<TicketModel>> allMyTickets;

    public AllMyTicketsViewModel(@NonNull Application application) {
        super(application);
        satAppRepository = new SatAppRepository();
    }

    public MutableLiveData<List<TicketModel>> getAllMyTickets(){
        allMyTickets = satAppRepository.getAllMyTickets();
        return allMyTickets;
    }

}