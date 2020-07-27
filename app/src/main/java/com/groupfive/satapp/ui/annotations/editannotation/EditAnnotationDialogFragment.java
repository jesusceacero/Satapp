package com.groupfive.satapp.ui.annotations.editannotation;

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

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.listeners.OnUpdateAnnotationDialogListener;
import com.groupfive.satapp.models.annotations.NewAnnotation;
import com.groupfive.satapp.models.annotations.UpdateAnnotation;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAnnotationDialogFragment extends DialogFragment {
    View v;
    Context ctx;
    EditText edCuerpo;
    SatAppService service;
    String idAnnotation;
    OnUpdateAnnotationDialogListener mListener;

    public EditAnnotationDialogFragment(Context ctx, String annotationId, OnUpdateAnnotationDialogListener onUpdateAnnotationDialogListener) {
        this.ctx = ctx;
        this.idAnnotation = annotationId;
        this.mListener = onUpdateAnnotationDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.edit_annotation_title));
        builder.setMessage(getResources().getString(R.string.edit_anntation_message));

        builder.setCancelable(true);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_annotation, null);
        builder.setView(v);

        edCuerpo = v.findViewById(R.id.editTextCuerpoEditAnnotation);

        builder.setPositiveButton(getResources().getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cuerpo = edCuerpo.getText().toString();

                if(cuerpo.isEmpty() ) {
                    edCuerpo.setError(getResources().getString(R.string.edit_annotation_corp_needed));
                } else {
                    service = SatAppServiceGenerator.createService(SatAppService.class);
                    UpdateAnnotation updateAnnotation = new UpdateAnnotation(cuerpo);
                    Call<NewAnnotation> call = service.updateAnotation(idAnnotation, updateAnnotation);
                    call.enqueue(new Callback<NewAnnotation>() {
                        @Override
                        public void onResponse(Call<NewAnnotation> call, Response<NewAnnotation> response) {
                            if(mListener != null){
                                mListener.onAnnotationUpdate();
                            }
                            Toast.makeText(MyApp.getContext(), getResources().getString(R.string.edit_annotation_succed), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<NewAnnotation> call, Throwable t) {
                            Toast.makeText(MyApp.getContext(), getResources().getString(R.string.edit_annotation_error), Toast.LENGTH_SHORT).show();
                        }
                    });

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
