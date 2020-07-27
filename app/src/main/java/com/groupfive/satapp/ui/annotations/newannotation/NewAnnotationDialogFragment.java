package com.groupfive.satapp.ui.annotations.newannotation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.groupfive.satapp.R;
import com.groupfive.satapp.models.annotations.NewAnnotation;
import com.groupfive.satapp.models.annotations.NewAnnotationBody;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewAnnotationDialogFragment extends DialogFragment {

    View v;
    EditText etCuerpo;
    Context ctx;
    Activity act;
    SatAppService service;
    String ticketId;

    public NewAnnotationDialogFragment(Context ctx, String id) {
        this.ctx = ctx;
        this.ticketId = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.new_annotation_title));
        builder.setMessage(getResources().getString(R.string.new_annotation_message));

        builder.setCancelable(true);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_annotation, null);
        builder.setView(v);

        etCuerpo= v.findViewById(R.id.editTextCorpNewAnnotation);

        builder.setPositiveButton(getResources().getString(R.string.create), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cuerpo = etCuerpo.getText().toString();

                if(cuerpo.isEmpty()) {
                    etCuerpo.setError(getResources().getString(R.string.edit_annotation_corp_needed));
                } else {
                    act = (Activity) ctx;
                    service = SatAppServiceGenerator.createService(SatAppService.class);
                    NewAnnotationBody newAnnotationBody = new NewAnnotationBody(ticketId, cuerpo);
                    Call<NewAnnotation> call = service.postAnnotation(newAnnotationBody);
                    call.enqueue(new Callback<NewAnnotation>() {
                        @Override
                        public void onResponse(Call<NewAnnotation> call, Response<NewAnnotation> response) {
                            Toast.makeText(act, getResources().getString(R.string.new_annotation_succed), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<NewAnnotation> call, Throwable t) {
                            Toast.makeText(act, getResources().getString(R.string.new_annotation_error), Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.dismiss();
                }
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

}
