package com.groupfive.satapp.ui.inventariable.newinventariable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.groupfive.satapp.R;
import com.groupfive.satapp.data.viewModel.InventariableViewModel;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.models.inventariable.TipoInventariable;
import com.groupfive.satapp.retrofit.service.SatAppInvService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;
import com.groupfive.satapp.ui.inventariable.allinventariable.MyInventariableRecyclerViewAdapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddInvActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    TextView tvTitle;
    EditText etName, etDescription, etLocation;
    Button btAction;
    Button btCancel;
    ImageView ivPhoto;
    Uri uriS;
    Spinner spinnerTypes;
    String id, photoCode;
    SatAppInvService service;
    ArrayAdapter arrayAdapter;
    InventariableViewModel inventariableViewModel;
    MyInventariableRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inv);

        service = SatAppServiceGenerator.createService(SatAppInvService.class);

        tvTitle = findViewById(R.id.textViewTitle);
        etName = findViewById(R.id.editTextName);
        etDescription = findViewById(R.id.editTextDescription);
        etLocation = findViewById(R.id.editTextUbicacion);
        ivPhoto = findViewById(R.id.imageViewPhoto);
        spinnerTypes = findViewById(R.id.spinnerType);
        btAction = findViewById(R.id.buttonAction);
        btCancel = findViewById(R.id.buttonCancel);


        Glide.with(AddInvActivity.this).load("https://cdn2.iconfinder.com/data/icons/photo-and-video/500/Landscape_moon_mountains_multiple_photo_photograph_pictury_sun-512.png").into(ivPhoto);

        arrayAdapter = new ArrayAdapter(AddInvActivity.this, R.layout.support_simple_spinner_dropdown_item, TipoInventariable.values());
        spinnerTypes.setAdapter(arrayAdapter);

        inventariableViewModel = ViewModelProviders.of(AddInvActivity.this).get(InventariableViewModel.class);

            tvTitle.setText(getResources().getString(R.string.addInv));

            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performFileSearch();
                }
            });

            btAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(etName.getText().toString().isEmpty() || etDescription.getText().toString().isEmpty() || etLocation.getText().toString().isEmpty() || uriS == null) {

                    } else {
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
                                    MultipartBody.Part.createFormData("imagen", "avatar", requestFile);

                            RequestBody nameRequest = RequestBody.create(MultipartBody.FORM,etName.getText().toString());
                            RequestBody typeRequest = RequestBody.create(MultipartBody.FORM,spinnerTypes.getSelectedItem().toString());
                            RequestBody descriptionRequest = RequestBody.create(MultipartBody.FORM, etDescription.getText().toString());
                            RequestBody locationRequest = RequestBody.create(MultipartBody.FORM, etLocation.getText().toString());

                            Call<Inventariable> call = service.addInventariable(body, typeRequest, nameRequest, descriptionRequest, locationRequest);

                                    call.enqueue(new Callback<Inventariable>() {
                                @Override
                                public void onResponse(Call<Inventariable> call, Response<Inventariable> response) {
                                    if(response.isSuccessful()) {
                                        Snackbar.make(v, getResources().getString(R.string.added_succes), Snackbar.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();

                                    }
                                }

                                @Override
                                public void onFailure(Call<Inventariable> call, Throwable t) {

                                }
                            });

                            finish();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });

            btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
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
                        .into(ivPhoto);
                uriS = uri;
            }
        }
    }


}
