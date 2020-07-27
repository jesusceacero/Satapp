package com.groupfive.satapp.ui.inventariable.editinventariable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.data.viewModel.InventariableViewModel;
import com.groupfive.satapp.models.inventariable.EditInventariable;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.retrofit.service.SatAppInvService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditInventariableFragment extends DialogFragment {
    View v;
    Context ctx;
    EditText etName, etLocation, etDescription;
    SatAppInvService service;
    InventariableViewModel inventariableViewModel;
    String id;

    public EditInventariableFragment(Context ctx, String id) {
        this.ctx = ctx;
        this.id = id;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        inventariableViewModel = ViewModelProviders.of(getActivity()).get(InventariableViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.edit_inv_dialog_title));
        builder.setMessage(getResources().getString(R.string.edit_inv_dialog_message));

        builder.setCancelable(true);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_inventariable, null);
        builder.setView(v);

        etName = v.findViewById(R.id.editTextName);
        etLocation = v.findViewById(R.id.editTextLocation);
        etDescription = v.findViewById(R.id.editTextDescription);

        getInventariable();

        builder.setPositiveButton(getResources().getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString();
                String location = etLocation.getText().toString();
                String description = etDescription.getText().toString();


                    service = SatAppServiceGenerator.createService(SatAppInvService.class);
                    EditInventariable editInventariable = new EditInventariable(name, location, description);
                    Call<Inventariable> call = service.putInventariable(id, editInventariable);
                    call.enqueue(new Callback<Inventariable>() {
                        @Override
                        public void onResponse(Call<Inventariable> call, Response<Inventariable> response) {
                            Toast.makeText(MyApp.getContext(), getResources().getString(R.string.edit_equip), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Inventariable> call, Throwable t) {
                            Toast.makeText(MyApp.getContext(), getResources().getString(R.string.edit_equip_error), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public void onDismiss(DialogInterface.OnDismissListener onDismissListener) {
    }

    public void getInventariable() {
        inventariableViewModel.getInventariable(id).observe(getActivity(), new Observer<Inventariable>() {
            @Override
            public void onChanged(Inventariable inventariable) {
                etName.setText(inventariable.getNombre());
                etDescription.setText(inventariable.getDescripcion());
                etLocation.setText(inventariable.getUbicacion());


            }
        });
    }
}

