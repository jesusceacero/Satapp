package com.groupfive.satapp.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.groupfive.satapp.data.repositories.SatAppRepository;
import com.groupfive.satapp.models.auth.AuthLogin;
import com.groupfive.satapp.models.auth.AuthLoginUser;

import java.util.List;

public class AddThecnicianViewModel extends AndroidViewModel {

    private SatAppRepository satAppRepository;
    private MutableLiveData<List<AuthLoginUser>> allUsers;

    public AddThecnicianViewModel(@NonNull Application application) {
        super(application);
        satAppRepository = new SatAppRepository();
    }

    public MutableLiveData<List<AuthLoginUser>> getAllUsers(){
        allUsers = satAppRepository.getAllUsers();
        return allUsers;
    }

}
