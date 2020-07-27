package com.groupfive.satapp.data.repositories;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.models.auth.AuthLoginUser;
import com.groupfive.satapp.models.auth.DtoName;
import com.groupfive.satapp.models.auth.Password;
import com.groupfive.satapp.retrofit.servicegenerator.LoginServiceGenerator;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSatAppRepository {

    SatAppService service;
    MutableLiveData<AuthLoginUser> user;
    MutableLiveData<List<AuthLoginUser>> allUser;
    MutableLiveData<List<AuthLoginUser>> usersValidated;
    MutableLiveData<AuthLoginUser> userModificate;
    MutableLiveData<AuthLoginUser> userPassword;
    MutableLiveData<AuthLoginUser> userId;
    SatAppService service2;

    public UserSatAppRepository() {
        service = SatAppServiceGenerator.createService(SatAppService.class);
        service2 = LoginServiceGenerator.createService(SatAppService.class);
    }

    public MutableLiveData<AuthLoginUser> getUser(){
        final MutableLiveData<AuthLoginUser> data = new MutableLiveData<>();

        Call<AuthLoginUser> call = service.getUser();
        call.enqueue(new Callback<AuthLoginUser>() {
            @Override
            public void onResponse(Call<AuthLoginUser> call, Response<AuthLoginUser> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                }else {
                    Toast.makeText(MyApp.getContext(), "Error al recibir el usuario.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthLoginUser> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
        user = data;
        return data;
    }


    public MutableLiveData<ResponseBody> getPicture(String id){
        final MutableLiveData<ResponseBody> data = new MutableLiveData<>();
        Call<ResponseBody> call = service.getImg(id);
        call .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                }else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        return data;
    }

    public MutableLiveData<List<AuthLoginUser>> getAllUsers(){
        final MutableLiveData<List<AuthLoginUser>> data = new MutableLiveData<>();

        Call<List<AuthLoginUser>> call = service.getAllUsers();
        call.enqueue(new Callback<List<AuthLoginUser>>() {
            @Override
            public void onResponse(Call<List<AuthLoginUser>> call, Response<List<AuthLoginUser>> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                    for (AuthLoginUser user: data.getValue()) {
                        List<String> lista = new ArrayList<>();
                        if (user.getName() == null || user.getName().isEmpty()){
                            user.setName("No definido");
                        }
                        if (user.getEmail() == null || user.getEmail().isEmpty()){
                            user.setEmail("No Definido");
                        }
                        lista.add(user.getName());
                        lista.add(user.getEmail());
                        user.setPalabrasClave(lista);
                    }

                }else {
                    Toast.makeText(MyApp.getContext(), "Error al recibir los usuarios.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<AuthLoginUser>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
        allUser = data;
        return data;
    }


    public MutableLiveData<List<AuthLoginUser>> getUsersValidate(){
        final MutableLiveData<List<AuthLoginUser>> data = new MutableLiveData<>();

        Call<List<AuthLoginUser>> call = service.getUsersValidated();
        call.enqueue(new Callback<List<AuthLoginUser>>() {
            @Override
            public void onResponse(Call<List<AuthLoginUser>> call, Response<List<AuthLoginUser>> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                }else {
                    Toast.makeText(MyApp.getContext(), "Error al recibir los usuarios sin validar.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<AuthLoginUser>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
        usersValidated = data;
        return data;
    }

    public void putValidated(String id){
        Call<AuthLoginUser> call = service.putValidated(id);
        call.enqueue(new Callback<AuthLoginUser>() {
            @Override
            public void onResponse(Call<AuthLoginUser> call, Response<AuthLoginUser> response) {
                if (response.isSuccessful()){
                    Log.i("Validated","Usuario Validado");
                }else {
                    Log.e("Validated","Error al devolver el usuario validado");
                }
            }

            @Override
            public void onFailure(Call<AuthLoginUser> call, Throwable t) {
                Log.e("Validated","Error al realizar la petición de validación");
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteUser(String id){
        Call<ResponseBody> call = service.deleteUser(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Log.i("Deleted","Usuario borrado");
                }else {
                    Log.e("Deleted","Error al devolver el usuario borrado.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Deleted","Error al realizar la paticion de borrado de usuario.");
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void putTecnico(String id){
        Call<AuthLoginUser> call = service.putTecnico(id);
        call.enqueue(new Callback<AuthLoginUser>() {
            @Override
            public void onResponse(Call<AuthLoginUser> call, Response<AuthLoginUser> response) {
                if (response.isSuccessful()){
                    Log.i("Tecnico","Usuario ascendido a tecnico.");
                }else {
                    Log.e("Tecnico","Error al devolver el usuario ascendido a tecnico.");
                }
            }

            @Override
            public void onFailure(Call<AuthLoginUser> call, Throwable t) {
                Log.e("Tecnico","Error realizar la petición de ascender a tecnico.");
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePhoto(String id, MultipartBody.Part avatar){
        Call<AuthLoginUser> call = service.updatePhoto(id,avatar);
        call.enqueue(new Callback<AuthLoginUser>() {
            @Override
            public void onResponse(Call<AuthLoginUser> call, Response<AuthLoginUser> response) {
                if (response.isSuccessful()){
                    Log.i("uptade","Foto actualizada correctamente");
                }else{
                    Log.e("update","Error al recibir el usuario cuando se cambia la foto");
                }
            }

            @Override
            public void onFailure(Call<AuthLoginUser> call, Throwable t) {
                Log.e("update","Error al realizar la peticion de actualización de foto");
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deletePhoto (String id){
        Call<ResponseBody> call = service.deletePhoto(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.i("deletePhoto","Foto borrada correctamente");
                }else{
                    Log.e("deletePhoto","Error al recibir el usuario sn foto");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("deletePhoto","Error al realizar la petición de borrado de foto");
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<AuthLoginUser> putUser(String id, String name){
        DtoName n = new DtoName(name);
        final MutableLiveData<AuthLoginUser> data = new MutableLiveData<>();
        Call<AuthLoginUser> call = service.putUser(id,n);
        call.enqueue(new Callback<AuthLoginUser>() {
            @Override
            public void onResponse(Call<AuthLoginUser> call, Response<AuthLoginUser> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                    Log.i("putUser","Usuario actualizado correctamente");
                }else {
                    Log.e("putUser","Error al recibir el usuario modificado");
                }
            }

            @Override
            public void onFailure(Call<AuthLoginUser> call, Throwable t) {
                Log.e("putUser","Error al realizar la petición de modificar usuario");
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
        userModificate = data;
        return data;
    }

    public MutableLiveData<AuthLoginUser> putPassword(String id,String authHeader, String password){
        Password p = new Password(password);
        final MutableLiveData<AuthLoginUser> data = new MutableLiveData<>();
        Call<AuthLoginUser> call = service2.putPassword(id,authHeader,p);
        call.enqueue(new Callback<AuthLoginUser>() {
            @Override
            public void onResponse(Call<AuthLoginUser> call, Response<AuthLoginUser> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                    Log.i("putPasswor","Contraseña cambiada correctamente");
                }else {
                    Log.e("putPasswor","Error al recibir el usuario con la contraseña cambiada");
                }
            }

            @Override
            public void onFailure(Call<AuthLoginUser> call, Throwable t) {
                Log.e("putPasswor","Error al realizar la petición de cambio de contraseña");
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
        userPassword = data;
        return data;
    }

    public MutableLiveData<AuthLoginUser> getUserId(String id){
        final MutableLiveData<AuthLoginUser> data = new MutableLiveData<>();
        Call<AuthLoginUser> call = service.getUserId(id);
        call.enqueue(new Callback<AuthLoginUser>() {
            @Override
            public void onResponse(Call<AuthLoginUser> call, Response<AuthLoginUser> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                    Log.i("getUserId","El usuario se a racogido correctamente");
                }else {
                    Log.e("getUserId","Error al recoger el usuario por id");
                }
            }

            @Override
            public void onFailure(Call<AuthLoginUser> call, Throwable t) {
                Log.e("getUserId","Error al realizar la petición por id");
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
            }
        });
        userId = data;
        return data;
    }
}
