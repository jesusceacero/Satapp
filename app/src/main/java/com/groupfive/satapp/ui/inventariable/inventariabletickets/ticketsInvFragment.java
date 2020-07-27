package com.groupfive.satapp.ui.inventariable.inventariabletickets;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.SharedPreferencesManager;
import com.groupfive.satapp.data.viewModel.InventariableViewModel;
import com.groupfive.satapp.listeners.IHistoryListener;
import com.groupfive.satapp.models.tickets.TicketModel;

import java.util.List;

public class ticketsInvFragment extends Fragment {

    private int mColumnCount = 1;
    private IHistoryListener mListener;
    private InventariableViewModel inventariableViewModel;
    private MyticketsInvRecyclerViewAdapter adapter;
    private List<TicketModel> ticketModelList;
    private Context context;

    public ticketsInvFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inventariableViewModel = ViewModelProviders.of(getActivity()).get(InventariableViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets_inv_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new MyticketsInvRecyclerViewAdapter(context, ticketModelList, mListener);

            recyclerView.setAdapter(adapter);

            loadTickets();



        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IHistoryListener) {
            mListener = (IHistoryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IHistoryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void loadTickets() {
        inventariableViewModel.getTicketFrom(SharedPreferencesManager.getStringValue("id")).observe(getActivity(), new Observer<List<TicketModel>>() {
            @Override
            public void onChanged(List<TicketModel> ticketModels) {
                ticketModelList = ticketModels;
                adapter.setData(ticketModels);
                adapter.notifyDataSetChanged();
            }
        });

    }

}
