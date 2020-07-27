package com.groupfive.satapp.ui.auth.register;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.groupfive.satapp.data.repositories.AuthSatAppRepository;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import com.groupfive.satapp.R;

public class RegisterActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    EditText email,passwor1,password2,name;
    Uri uriS;
    Button register;
    ImageView avatar;
    AuthSatAppRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        repository = new AuthSatAppRepository();

        email = findViewById(R.id.editTextEmailRegis);
        passwor1 = findViewById(R.id.editTextPassword1);
        password2 = findViewById(R.id.editTextPassword2);
        name = findViewById(R.id.editTextName);
        avatar = findViewById(R.id.imageViewUpload);
        register = findViewById(R.id.buttonRegis);
        uriS = null;

        Glide
                .with(this)
                .load(R.drawable.ic_upload)
                .into(avatar);

        register.setEnabled(true);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        email.getText().toString().isEmpty() || name.getText().toString().isEmpty() || passwor1.getText().toString().isEmpty() || password2.getText().toString().isEmpty()
                ){
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.register_complete_all_fields), Toast.LENGTH_SHORT).show();

                }else if(
                        !passwor1.getText().toString().equals(password2.getText().toString())
                ){
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.register_paswords_not_match), Toast.LENGTH_SHORT).show();
                }else {
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


                            RequestBody emailRequest = RequestBody.create(MultipartBody.FORM,email.getText().toString());
                            RequestBody nameRequest = RequestBody.create(MultipartBody.FORM,name.getText().toString());
                            RequestBody passwordRequest = RequestBody.create(MultipartBody.FORM, passwor1.getText().toString());


                            repository.register(body,emailRequest,passwordRequest,nameRequest);
                            finish();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {

                        RequestBody emailRequest = RequestBody.create(MultipartBody.FORM,email.getText().toString());
                        RequestBody nameRequest = RequestBody.create(MultipartBody.FORM,name.getText().toString());
                        RequestBody passwordRequest = RequestBody.create(MultipartBody.FORM, passwor1.getText().toString());


                        repository.register(null,emailRequest,passwordRequest,nameRequest);
                        finish();
                    }
                }
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
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
                        .transform(new CircleCrop())
                        .into(avatar);
                uriS = uri;
            }
        }
    }
}
