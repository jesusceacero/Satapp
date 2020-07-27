package com.groupfive.satapp.ui.inventariable.allinventariable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.commons.NameComparator;
import com.groupfive.satapp.data.viewModel.InventariableViewModel;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.listeners.IInventariableListener;
import com.groupfive.satapp.ui.inventariable.locationinventariable.LocationActivity;
import com.groupfive.satapp.ui.inventariable.newinventariable.AddInvActivity;

import java.util.Collections;
import java.util.List;

public class InventariableFragment extends Fragment {

    private int mColumnCount = 1;
    private IInventariableListener mListener;
    private InventariableViewModel inventariableViewModel;
    private List<Inventariable> inventariableList;
    private MyInventariableRecyclerViewAdapter adapter;
    private Context context;
    private RecyclerView recyclerView;


    public InventariableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inventariableViewModel = ViewModelProviders.of(getActivity()).get(InventariableViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setVisibility(View.GONE);
        loadInventariable();
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addButton:
                Intent i = new Intent(MyApp.getContext(), AddInvActivity.class);
                startActivity(i);
                break;
            case R.id.showLocations:
                Intent j = new Intent(MyApp.getContext(), LocationActivity.class);
                startActivity(j);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.inv_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventariable_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


            adapter = new MyInventariableRecyclerViewAdapter(inventariableList, mListener, context);
            recyclerView.setAdapter(adapter);

            loadInventariable();


        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IInventariableListener) {
            mListener = (IInventariableListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IInventariableListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void loadInventariable() {
        inventariableViewModel.getAllInventariables().observe(getActivity(), new Observer<List<Inventariable>>() {
            @Override
            public void onChanged(List<Inventariable> inventariables) {
                inventariableList = inventariables;
                Collections.sort(inventariableList, new NameComparator());
                adapter.setData(inventariableList);
                adapter.notifyDataSetChanged();
            }
        });
    }


}
