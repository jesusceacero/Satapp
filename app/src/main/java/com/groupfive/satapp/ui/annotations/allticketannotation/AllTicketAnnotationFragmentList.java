package com.groupfive.satapp.ui.annotations.allticketannotation;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.listeners.ITicketAnnotationListener;
import com.groupfive.satapp.listeners.OnUpdateAnnotationDialogListener;
import com.groupfive.satapp.models.tickets.TicketAnotaciones;
import com.groupfive.satapp.models.tickets.TicketWithAnnotations;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllTicketAnnotationFragmentList extends Fragment implements OnUpdateAnnotationDialogListener {

    private int mColumnCount = 1;
    private ITicketAnnotationListener mListener;
    Context context;
    RecyclerView recyclerView;
    MyAllTicketAnnotationRecyclerViewAdapter adapter;
    List<TicketAnotaciones> annotationList = new ArrayList<>();
    SatAppService service;
    OnUpdateAnnotationDialogListener onUpdateAnnotationDialogListener;

    public AllTicketAnnotationFragmentList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_ticket_annotation_list_list, container, false);

        if (view instanceof RecyclerView) {
            context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            onUpdateAnnotationDialogListener = this;
            adapter = new MyAllTicketAnnotationRecyclerViewAdapter(context,annotationList, mListener, onUpdateAnnotationDialogListener);
            recyclerView.setAdapter(adapter);
            loadAnnotations();
        }
        return view;
    }

    public void loadAnnotations(){
        service = SatAppServiceGenerator.createService(SatAppService.class);
        String id = MyApp.getContext().getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE).getString(Constants.SHARED_PREFERENCES_TICKET_ID, null);
        Call<TicketWithAnnotations> call = service.getTicketByIdForAnnotaions(id);
        call.enqueue(new Callback<TicketWithAnnotations>() {
            @Override
            public void onResponse(Call<TicketWithAnnotations> call, Response<TicketWithAnnotations> response) {
                if(response.body().getAnotaciones() != null){
                    annotationList = response.body().getAnotaciones();
                    adapter.setData(annotationList);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.ticket_have_cero_annotations), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TicketWithAnnotations> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_loading_annotations), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ITicketAnnotationListener) {
            mListener = (ITicketAnnotationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ITicketAnnotationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAnnotationUpdate() {
        loadAnnotations();
    }
}
