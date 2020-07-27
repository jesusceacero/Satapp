package com.groupfive.satapp.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.groupfive.satapp.data.repositories.SatAppRepository;
import com.groupfive.satapp.models.tickets.TicketModel;

public class TicketByIdViewModel extends AndroidViewModel {

    private SatAppRepository satAppRepository;
    private MutableLiveData<TicketModel> ticketByIs;
    private String ticketId;

    public TicketByIdViewModel(@NonNull Application application) {
        super(application);
        satAppRepository = new SatAppRepository();
    }

    public MutableLiveData<TicketModel> getTicketById(){
        ticketByIs = satAppRepository.getTicketById(ticketId);
        return ticketByIs;
    }

    public void setTicketId(String id){
        ticketId = id;
    }

}
