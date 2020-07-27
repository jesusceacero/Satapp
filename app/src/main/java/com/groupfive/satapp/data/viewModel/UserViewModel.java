package com.groupfive.satapp.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.groupfive.satapp.data.repositories.UserSatAppRepository;
import com.groupfive.satapp.models.auth.AuthLoginUser;

import java.security.PublicKey;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;


public class UserViewModel extends AndroidViewModel {

    private UserSatAppRepository userSatAppRepository;
    private MutableLiveData<AuthLoginUser> user;
    private MutableLiveData<List<AuthLoginUser>> allUser;
    private MutableLiveData<List<AuthLoginUser>> usersValidated;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userSatAppRepository = new UserSatAppRepository();
    }

    public MutableLiveData<AuthLoginUser> getUser(){
        user = userSatAppRepository.getUser();
        return user;
    }


    public MutableLiveData<List<AuthLoginUser>> getAllUser(){
        allUser = userSatAppRepository.getAllUsers();
        return allUser;
    }


    public MutableLiveData<List<AuthLoginUser>> getUsersValidated(){
        usersValidated = userSatAppRepository.getUsersValidate();
        return usersValidated;
    }

    public MutableLiveData<ResponseBody> getPicture(String id){
        return userSatAppRepository.getPicture(id);
    }

    public void putValidated(String id){
        userSatAppRepository.putValidated(id);
    }

    public void deleteUser(String id){
        userSatAppRepository.deleteUser(id);
    }

    public void putTecnico(String id){
        userSatAppRepository.putTecnico(id);
    }

    public void updatePhoto(String id, MultipartBody.Part avatar){
        userSatAppRepository.updatePhoto(id,avatar);
    }

    public void deletePhoto(String id){
        userSatAppRepository.deletePhoto(id);
    }

    public MutableLiveData<AuthLoginUser> putUser (String id, String name){
        return userSatAppRepository.putUser(id,name);
    }

    public MutableLiveData<AuthLoginUser> putPassword (String id,String authHeader,String password){
        return userSatAppRepository.putPassword(id,authHeader,password);
    }

    public MutableLiveData<AuthLoginUser> getUserId(String id){
        return userSatAppRepository.getUserId(id);
    }
}
