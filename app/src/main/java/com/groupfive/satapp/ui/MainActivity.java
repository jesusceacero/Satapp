package com.groupfive.satapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import com.bumptech.glide.Glide;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.groupfive.satapp.listeners.IInventariableListener;
import com.groupfive.satapp.listeners.IHistoryListener;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.data.repositories.UserSatAppRepository;
import com.groupfive.satapp.data.viewModel.UserViewModel;
import com.groupfive.satapp.models.auth.AuthLoginUser;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.listeners.IAllTicketsListener;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.ui.auth.login.LoginActivity;
import com.groupfive.satapp.ui.tickets.ticketdetail.TicketDetailActivity;
import com.groupfive.satapp.ui.users.userprofile.ProfileActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements IInventariableListener, IAllTicketsListener, IHistoryListener {
    private AppBarConfiguration mAppBarConfiguration;
    private UserViewModel userViewModel;
    private AuthLoginUser user;
    private UserSatAppRepository userSatAppRepository;
    private ImageView ivFotoPerfil;
    private TextView nameUser, emailUser;
    public static final int REQUEST_READ_CALENDAR = 79;
    public static final int REQUEST_WRITE_CALENDAR = 78;
    private Menu menuLateral;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        userSatAppRepository = new UserSatAppRepository();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Menu
        menuLateral = navigationView.getMenu();

        // Header
        View header = navigationView.getHeaderView(0);
        ivFotoPerfil = header.findViewById(R.id.imageViewFotoPerfil);
        nameUser = header.findViewById(R.id.textViewNameUser);
        emailUser = header.findViewById(R.id.textViewEmailUser);
        ivFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(MyApp.getContext(),
                        ProfileActivity.class);
                startActivity(i);
            }
        });

        userViewModel.getUser().observe(this, new Observer<AuthLoginUser>() {
            @Override
            public void onChanged(AuthLoginUser authLoginUser) {
                user = authLoginUser;
                nameUser.setText(user.name);
                emailUser.setText(user.email);

                if (!user.getRole().equals("admin")){
                    menuLateral.findItem(R.id.nav_slideshow).setVisible(false);
                }

                if (user.picture != null) {
                    userViewModel.getPicture(user.id).observeForever(new Observer<ResponseBody>() {
                        @Override
                        public void onChanged(ResponseBody responseBody) {
                            Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
                            Glide.with(MyApp.getContext())
                                    .load(bmp)
                                    .circleCrop()
                                    .into(ivFotoPerfil);
                        }
                    });
                }else {
                    Glide.with(MyApp.getContext())
                            .load(R.drawable.ic_perfil)
                            .circleCrop()
                            .into(ivFotoPerfil);
                }

            }
        });

        requestPermissionCalendar();

    }

    private void requestPermissionCalendar() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CALENDAR)) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALENDAR},
                    REQUEST_READ_CALENDAR);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_CALENDAR)) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR},
                    REQUEST_WRITE_CALENDAR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPreferences settings = this.getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onInventariableClick(Inventariable iv) {
    }

    @Override
    public void onAllTicketsItemClick(TicketModel ticketModel) {
        Intent i = new Intent(MainActivity.this, TicketDetailActivity.class);
        i.putExtra(Constants.EXTRA_TICKET_ID, String.valueOf(ticketModel.getId()));
        startActivity(i);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        userViewModel.getUser().observe(this, new Observer<AuthLoginUser>() {
            @Override
            public void onChanged(AuthLoginUser authLoginUser) {
                user = authLoginUser;
                nameUser.setText(user.name);
                emailUser.setText(user.email);

                if (user.picture != null) {
                    userViewModel.getPicture(user.id).observeForever(new Observer<ResponseBody>() {
                        @Override
                        public void onChanged(ResponseBody responseBody) {
                            Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
                            Glide.with(MyApp.getContext())
                                    .load(bmp)
                                    .circleCrop()
                                    .into(ivFotoPerfil);
                        }
                    });
                }else {
                    Glide.with(MyApp.getContext())
                            .load(R.drawable.ic_perfil)
                            .circleCrop()
                            .into(ivFotoPerfil);
                }

            }
        });
    }

    @Override
    public void onHistoryClick(TicketModel ticketModel) {

    }
}
