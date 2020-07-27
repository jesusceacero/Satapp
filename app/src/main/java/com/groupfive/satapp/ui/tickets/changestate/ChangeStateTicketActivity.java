package com.groupfive.satapp.ui.tickets.changestate;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.models.tickets.ChangeTicketState;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeStateTicketActivity extends AppCompatActivity implements View.OnClickListener{

    String ticketId;
    SatAppService service;
    Button btnPendiente, btnAsignada, btnEnProceso, btnSolucionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_state_ticket);

        btnPendiente = findViewById(R.id.buttonStatePendienteAsignacion);
        btnAsignada = findViewById(R.id.buttonStateAsignada);
        btnEnProceso = findViewById(R.id.buttonStateEnProceso);
        btnSolucionada = findViewById(R.id.buttonStateSolucionada);

        btnPendiente.setOnClickListener(this);
        btnAsignada.setOnClickListener(this);
        btnEnProceso.setOnClickListener(this);
        btnSolucionada.setOnClickListener(this);

        ticketId = getIntent().getExtras().get(Constants.EXTRA_TICKET_ID).toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonStatePendienteAsignacion:
                editState("PENDIENTE_ASIGNACION");
                break;
            case R.id.buttonStateAsignada:
                editState("ASIGNADA");
                break;
            case R.id.buttonStateEnProceso:
                editState("EN_PROCESO");
                break;
            case R.id.buttonStateSolucionada:
                editState("SOLUCIONADA");
                break;
        }
    }

    public void editState(String state){
        service = SatAppServiceGenerator.createService(SatAppService.class);
        ChangeTicketState changeTicketState = new ChangeTicketState(state);
        Call<TicketModel> call = service.updateTicketState(ticketId, changeTicketState);
        call.enqueue(new Callback<TicketModel>() {
            @Override
            public void onResponse(Call<TicketModel> call, Response<TicketModel> response) {
                Toast.makeText(ChangeStateTicketActivity.this, getResources().getString(R.string.edit_state_succed), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<TicketModel> call, Throwable t) {
                Toast.makeText(ChangeStateTicketActivity.this, getResources().getString(R.string.edit_state_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
