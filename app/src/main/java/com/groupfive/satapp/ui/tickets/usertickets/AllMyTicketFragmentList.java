package com.groupfive.satapp.ui.tickets.usertickets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.data.viewModel.AllMyTicketsViewModel;
import com.groupfive.satapp.listeners.IMyTicketListener;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.ui.auth.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;


public class AllMyTicketFragmentList extends Fragment {

    private int mColumnCount = 1;
    private IMyTicketListener mListener;
    Context context;
    RecyclerView recyclerView;
    List<TicketModel> myTicketList = new ArrayList<>();
    MyAllMyTicketRecyclerViewAdapter adapter;
    AllMyTicketsViewModel allMyTicketsViewModel;
    private MenuItem busqueda;

    public AllMyTicketFragmentList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        allMyTicketsViewModel = new ViewModelProvider(getActivity()).get(AllMyTicketsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_my_ticket_list_list, container, false);

        if (view instanceof RecyclerView) {
            context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyAllMyTicketRecyclerViewAdapter(context , myTicketList, mListener);
            recyclerView.setAdapter(adapter);
            loadAllMyTickets();
            Toast.makeText(context, getResources().getString(R.string.user_tickets_this_are_yours), Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public void loadAllMyTickets(){
        allMyTicketsViewModel.getAllMyTickets().observe(getActivity(), new Observer<List<TicketModel>>() {
            @Override
            public void onChanged(List<TicketModel> list) {
                myTicketList = list;
                adapter.setData(myTicketList);
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IMyTicketListener) {
            mListener = (IMyTicketListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IMyTicketListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setVisibility(View.GONE);
        loadAllMyTickets();
        recyclerView.setVisibility(View.VISIBLE);
    }

    public List<TicketModel> busqueda(String palabraClave){
        List<TicketModel> result = new ArrayList<>();
        for (TicketModel ticket : myTicketList ){
            for (String palabraClaveList : ticket.getPalabrasClave()){
                if(palabraClaveList.equalsIgnoreCase(palabraClave) || palabraClaveList.toLowerCase().contains(palabraClave.toLowerCase())){
                    if (!result.contains(ticket)){
                        result.add(ticket);
                    }
                }
            }
        }
        adapter.setData(result);
        return result;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_my_tickets,menu);
        busqueda = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) busqueda.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<TicketModel> lista = busqueda(newText);
                cargarBusqueda(lista);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void cargarBusqueda (List<TicketModel> busqueda){
        adapter.setData(busqueda);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPreferences settings = getActivity().getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
