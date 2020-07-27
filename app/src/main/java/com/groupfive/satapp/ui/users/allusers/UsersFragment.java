package com.groupfive.satapp.ui.users.allusers;

import android.content.Context;
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
import android.widget.Button;
import android.widget.SearchView;

import com.groupfive.satapp.R;
import com.groupfive.satapp.data.viewModel.UserViewModel;
import com.groupfive.satapp.models.auth.AuthLoginUser;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private Context context;
    private RecyclerView users;
    private UserViewModel userViewModel;
    private List<AuthLoginUser> listUsers;
    private MyUsersRecyclerViewAdapter adapter;
    private Button allUsers,allValidated;
    private MenuItem busqueda;


    public UsersFragment() {
    }

    public static UsersFragment newInstance(int columnCount) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);
        users = view.findViewById(R.id.listUsers);
        allUsers = view.findViewById(R.id.buttonAllUsers);
        allValidated = view.findViewById(R.id.buttonAllValidated);
        context = view.getContext();

        loadUser();
        allUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUser();
            }
        });
        allValidated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadValidated();
            }
        });

        return view;
    }

    public void loadUser(){
        listUsers = new ArrayList<>();
        if (mColumnCount <= 1) {
            users.setLayoutManager(new LinearLayoutManager(context));
        } else {
            users.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = new MyUsersRecyclerViewAdapter(
                context,
                listUsers,
                userViewModel,
                false
        );
        users.setAdapter(adapter);
        userViewModel.getAllUser().observe(getActivity(), new Observer<List<AuthLoginUser>>() {
            @Override
            public void onChanged(List<AuthLoginUser> authLoginUsers) {
                listUsers.addAll(authLoginUsers);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void loadValidated(){
        listUsers = new ArrayList<>();
        if (mColumnCount <= 1) {
            users.setLayoutManager(new LinearLayoutManager(context));
        } else {
            users.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = new MyUsersRecyclerViewAdapter(
                context,
                listUsers,
                userViewModel,
                true
        );
        users.setAdapter(adapter);
        userViewModel.getUsersValidated().observe(getActivity(), new Observer<List<AuthLoginUser>>() {
            @Override
            public void onChanged(List<AuthLoginUser> authLoginUsers) {
                listUsers.addAll(authLoginUsers);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public List<AuthLoginUser> busqueda(String palabraClave){
        List<AuthLoginUser> result = new ArrayList<>();
        for (AuthLoginUser user : listUsers ){
            for (String palabraClaveList : user.getPalabrasClave()){
                if(palabraClaveList.equalsIgnoreCase(palabraClave) || palabraClaveList.toLowerCase().contains(palabraClave.toLowerCase())){
                    if (!result.contains(user)){
                        result.add(user);
                    }
                }
            }
        }
        adapter.setData(result);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onPrepareOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_user,menu);
        busqueda = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) busqueda.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<AuthLoginUser> lista = busqueda(newText);
                cargarBusqueda(lista);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void cargarBusqueda (List<AuthLoginUser> busqueda){
        adapter.setData(busqueda);
        adapter.notifyDataSetChanged();
    }
}
