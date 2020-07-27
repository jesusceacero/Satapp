package com.groupfive.satapp.ui.users.userprofile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.data.repositories.UserSatAppRepository;
import com.groupfive.satapp.data.viewModel.UserViewModel;
import com.groupfive.satapp.models.auth.AuthLoginUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ProfileActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private UserViewModel userViewModel;
    private UserSatAppRepository userSatAppRepository;
    private AuthLoginUser user;
    private TextView nombre, email;
    private CircularImageView circularImageView;
    private FloatingActionButton fab2;
    private Uri uriS;
    private EditText nameE,passawor1,passswor2,emailPass,passwordActual;
    private Button savePass, saveDate;
    private ImageView check, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userSatAppRepository = new UserSatAppRepository();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        nombre = findViewById(R.id.textViewNameProfile);
        email = findViewById(R.id.textViewEmailProfile);;
        fab2 = findViewById(R.id.fab2);
        nameE = findViewById(R.id.editTextNameProfile);
        passawor1 = findViewById(R.id.editTextPassworProfile);
        passswor2 = findViewById(R.id.editTextPassword2Pfrofile);
        savePass = findViewById(R.id.buttonSavePassProfile);
        saveDate = findViewById(R.id.buttonSaveDateProfile);
        check = findViewById(R.id.imageViewCheckPhoto);
        cancel = findViewById(R.id.imageViewCancelPhoto);
        emailPass = findViewById(R.id.editTextEmailPassword);
        passwordActual = findViewById(R.id.editTextPasswordActual);

        check.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);

        circularImageView = findViewById(R.id.imageViewPhotoProfileAdmin);
        circularImageView.setBorderWidth(3);
        circularImageView.setBorderColor(Color.WHITE);

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });


        userViewModel.getUser().observe(this, new Observer<AuthLoginUser>() {
            @Override
            public void onChanged(AuthLoginUser authLoginUser) {
                user = authLoginUser;
                nombre.setText(user.getName());
                email.setText(user.getEmail());
                nameE.setText(user.getName());

                if (user.picture != null) {
                    userViewModel.getPicture(user.id).observeForever(new Observer<ResponseBody>() {
                        @Override
                        public void onChanged(ResponseBody responseBody) {
                            Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
                            Glide.with(MyApp.getContext())
                                    .load(bmp)
                                    .thumbnail(Glide.with(ProfileActivity.this).load(R.drawable.loading_gif))
                                    .centerCrop()
                                    .into(circularImageView);
                        }
                    });
                }else {
                    Glide.with(MyApp.getContext())
                            .load(R.drawable.ic_perfil)
                            .thumbnail(Glide.with(ProfileActivity.this).load(R.drawable.loading_gif))
                            .centerCrop()
                            .into(circularImageView);
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ProfileActivity.this);
                alertDialogBuilder.setTitle(R.string.validacion);
                alertDialogBuilder
                        .setMessage(R.string.mensageValidacion)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                userViewModel.deletePhoto(user.id);

                                Glide.with(MyApp.getContext())
                                        .load(R.drawable.ic_perfil)
                                        .thumbnail(Glide.with(ProfileActivity.this).load(R.drawable.loading_gif))
                                        .centerCrop()
                                        .into(circularImageView);
                            }
                        })
                        .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriS != null) {

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uriS);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        int cantBytes;
                        byte[] buffer = new byte[1024*4];

                        while ((cantBytes = bufferedInputStream.read(buffer,0,1024*4)) != -1) {
                            baos.write(buffer,0,cantBytes);
                        }


                        RequestBody requestFile =
                                RequestBody.create(baos.toByteArray(),
                                        MediaType.parse(getContentResolver().getType(uriS)));


                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("avatar", "avatar", requestFile);

                        userViewModel.updatePhoto(user.id,body);
                        Toast.makeText(ProfileActivity.this, R.string.savePhoto, Toast.LENGTH_SHORT).show();
                        check.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(ProfileActivity.this, R.string.selecPhoto, Toast.LENGTH_SHORT).show();
                    check.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriS = null;
                userViewModel.getUser().observe(ProfileActivity.this, new Observer<AuthLoginUser>() {
                    @Override
                    public void onChanged(AuthLoginUser authLoginUser) {
                        if (user.picture != null) {
                            userViewModel.getPicture(user.id).observeForever(new Observer<ResponseBody>() {
                                @Override
                                public void onChanged(ResponseBody responseBody) {
                                    Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
                                    Glide.with(MyApp.getContext())
                                            .load(bmp)
                                            .thumbnail(Glide.with(ProfileActivity.this).load(R.drawable.loading_gif))
                                            .centerCrop()
                                            .into(circularImageView);
                                }
                            });
                        }else {
                            Glide.with(MyApp.getContext())
                                    .load(R.drawable.ic_perfil)
                                    .thumbnail(Glide.with(ProfileActivity.this).load(R.drawable.loading_gif))
                                    .centerCrop()
                                    .into(circularImageView);
                        }

                    }
                });
                check.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
        });

        saveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.putUser(user.getId(),nameE.getText().toString()).observeForever(new Observer<AuthLoginUser>() {
                    @Override
                    public void onChanged(AuthLoginUser authLoginUser) {
                        user = authLoginUser;
                        nombre.setText(user.getName());
                    }
                });
            }
        });

        savePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passawor1.getText().toString().equals(passswor2.getText().toString())){
                    String base = emailPass.getText().toString() + ":" + passwordActual.getText().toString();
                    String authHeader = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                    userViewModel.putPassword(user.getId(),authHeader,passawor1.getText().toString());
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.password_edit_succed), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.register_paswords_not_match), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("Filechooser URI", "Uri: " + uri.toString());
                Glide
                        .with(this)
                        .load(uri)
                        .thumbnail(Glide.with(ProfileActivity.this).load(R.drawable.loading_gif))
                        .centerCrop()
                        .into(circularImageView);
                uriS = uri;
                check.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
            }
        }
    }
}
