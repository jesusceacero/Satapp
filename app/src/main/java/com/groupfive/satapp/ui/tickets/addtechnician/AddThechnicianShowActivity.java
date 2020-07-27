package com.groupfive.satapp.ui.tickets.addtechnician;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.listeners.IAddTechnicianListener;
import com.groupfive.satapp.models.auth.AuthLoginUser;
import com.groupfive.satapp.models.tickets.AddTechnician;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddThechnicianShowActivity extends AppCompatActivity implements IAddTechnicianListener {

    String ticketId;
    SatAppService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thechnician_show);

        ticketId = getIntent().getExtras().get(Constants.EXTRA_TICKET_ID).toString();

    }

    @Override
    public void onAllTicketsItemClick(AuthLoginUser authLoginUser) {
        service = SatAppServiceGenerator.createService(SatAppService.class);
        AddTechnician addTechnician = new AddTechnician(authLoginUser.getId());
        Call<TicketModel> call = service.updateTickeAddTechnician(ticketId, addTechnician);
        call.enqueue(new Callback<TicketModel>() {
            @Override
            public void onResponse(Call<TicketModel> call, Response<TicketModel> response) {
                Toast.makeText(AddThechnicianShowActivity.this, getResources().getString(R.string.technician_added), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<TicketModel> call, Throwable t) {
                Toast.makeText(AddThechnicianShowActivity.this, getResources().getString(R.string.technician_addded_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
