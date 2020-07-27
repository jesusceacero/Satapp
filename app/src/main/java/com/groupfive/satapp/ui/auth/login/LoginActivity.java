package com.groupfive.satapp.ui.auth.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.commons.SharedPreferencesManager;
import com.groupfive.satapp.models.auth.AuthLogin;
import com.groupfive.satapp.models.auth.AuthLoginUser;
import com.groupfive.satapp.retrofit.servicegenerator.LoginServiceGenerator;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;
import com.groupfive.satapp.ui.MainActivity;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.ui.auth.register.RegisterActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ImageView logo;
    EditText username, password;
    Button login, register;
    SatAppService service;
    SatAppService serviceIsLogged;
    CardView cardView;
    ProgressBar progressBar;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        service = LoginServiceGenerator.createService(SatAppService.class);

        logo = findViewById(R.id.imageViewLogo);
        username = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword2Pfrofile);
        login = findViewById(R.id.buttonLogin);
        register = findViewById(R.id.buttonRegister);
        cardView = findViewById(R.id.cardViewLogin);
        progressBar = findViewById(R.id.progressBarLogin);

        Glide.with(this)
                .load(R.drawable.satapplogo)
                .transform(new CircleCrop())
                .into(logo);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(username.getText().toString(),password.getText().toString());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(MyApp.getContext(),
                        RegisterActivity.class);
                startActivity(i);
            }
        });

        token = MyApp.getContext().getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE).getString(Constants.SHARED_PREFERENCES_AUTH_TOKEN, null);
        if(token != null) {
            checkIsLogged();
        }

    }

    public void login(String username, String password){
        String base = username + ":" + password;
        String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Call<AuthLogin> call = service.postLogin(authHeader, Constants.MASTER_ACCES_TOKEN);
        call.enqueue(new Callback<AuthLogin>() {
            @Override
            public void onResponse(Call<AuthLogin> call, Response<AuthLogin> response) {
                if (response.isSuccessful()) {
                    SharedPreferencesManager.setStringValue(Constants.SHARED_PREFERENCES_AUTH_TOKEN,response.body().getToken());
                    SharedPreferencesManager.setStringValue(Constants.SHARED_PREFERENCES_EMAIL,response.body().getUser().getEmail());
                    SharedPreferencesManager.setStringValue(Constants.SHARED_PREFERENCES_ROLE,response.body().getUser().getRole());
                    Intent i =  new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(MyApp.getContext(), ""+response.message(), Toast.LENGTH_SHORT).show();
                    Log.i("user", "viene vacio");
                }
            }

            @Override
            public void onFailure(Call<AuthLogin> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
                Log.i("user", "peor");
            }
        });
    }

    public void checkIsLogged(){
        cardView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if(token != null){
            serviceIsLogged = SatAppServiceGenerator.createService(SatAppService.class);
            Call<AuthLoginUser> call = serviceIsLogged.getUser();
            call.enqueue(new Callback<AuthLoginUser>() {
                @Override
                public void onResponse(Call<AuthLoginUser> call, Response<AuthLoginUser> response) {
                    if(response.code() != 401){
                        Intent i =  new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_token_expired), Toast.LENGTH_SHORT).show();
                        cardView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<AuthLoginUser> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_in_the_connection), Toast.LENGTH_SHORT).show();
                    cardView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            cardView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

}
