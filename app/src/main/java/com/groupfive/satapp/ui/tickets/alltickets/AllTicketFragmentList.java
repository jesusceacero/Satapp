package com.groupfive.satapp.ui.tickets.alltickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.data.viewModel.GetAllTicketsViewModel;
import com.groupfive.satapp.listeners.IAllTicketsListener;
import com.groupfive.satapp.models.auth.AuthLoginUser;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.ui.MainActivity;
import com.groupfive.satapp.ui.tickets.assignedtickets.ShowAssignedTicketsActivity;
import com.groupfive.satapp.ui.tickets.newticket.NewTicketDialogFragment;
import com.groupfive.satapp.ui.tickets.usertickets.ShowAllMyTicketsActivity;

import java.util.ArrayList;
import java.util.List;

public class AllTicketFragmentList extends Fragment {

    private int mColumnCount = 1;
    private IAllTicketsListener mListener;
    MyAllTicketRecyclerViewAdapter adapter;
    Context context;
    RecyclerView recyclerView;
    GetAllTicketsViewModel getAllTicketsViewModel;
    List<TicketModel> ticketList = new ArrayList<>();
    private MenuItem busqueda;
    String userRole;

    public AllTicketFragmentList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAllTicketsViewModel =new ViewModelProvider(getActivity()).get(GetAllTicketsViewModel.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_fragment_ticket_list, container, false);

        if (view instanceof RecyclerView) {
            context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyAllTicketRecyclerViewAdapter(getActivity(), ticketList, mListener);
            recyclerView.setAdapter(adapter);
            loadAllTickets();
        }
        return view;
    }

    public void loadAllTickets(){
        getAllTicketsViewModel.getAllTickets().observe(getActivity(), new Observer<List<TicketModel>>() {
            @Override
            public void onChanged(List<TicketModel> list) {
                ticketList = list;
                adapter.setData(ticketList);
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IAllTicketsListener) {
            mListener = (IAllTicketsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IAllTicketsListener");
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
        loadAllTickets();
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onPrepareOptionsMenu(menu);
        userRole = getActivity().getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE).getString(Constants.SHARED_PREFERENCES_ROLE, null);
        if(userRole.equals(Constants.ROLE_TECNICO)){
            getActivity().getMenuInflater().inflate(R.menu.technician_menu_all_tickets, menu);
        } else {
            getActivity().getMenuInflater().inflate(R.menu.menu_all_tickets, menu);
        }
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

    public List<TicketModel> busqueda(String palabraClave){
        List<TicketModel> result = new ArrayList<>();
        for (TicketModel tiket : ticketList ){
            for (String palabraClaveList : tiket.getPalabrasClave()){
                if(palabraClaveList.equalsIgnoreCase(palabraClave) || palabraClaveList.toLowerCase().contains(palabraClave.toLowerCase())){
                    if (!result.contains(tiket)){
                        result.add(tiket);
                    }
                }
            }
        }
        adapter.setData(result);
        return result;
    }

    public void cargarBusqueda (List<TicketModel> busqueda){
        adapter.setData(busqueda);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_asigned_tickets:
                Intent assigneds = new Intent(getActivity(), ShowAssignedTicketsActivity.class);
                startActivity(assigneds);
                break;
            case R.id.action_created_tickets:
                Intent created = new Intent(getActivity(), ShowAllMyTicketsActivity.class);
                startActivity(created);
                break;
            case R.id.action_add_ticket:
                NewTicketDialogFragment dialog = new NewTicketDialogFragment(getActivity(), null);
                dialog.show(getActivity().getSupportFragmentManager(), "NewTicketDialogFragment");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
