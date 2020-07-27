package com.groupfive.satapp.ui.tickets.newticket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTicketDialogFragment extends DialogFragment {

    View v;
    Context ctx;
    String inventariableId;
    private static final int READ_REQUEST_CODE = Constants.IMAGE_READ_REQUEST_CODE;
    EditText edTitle, edDescription;
    Button btnUploadFoto;
    ImageView ivFotoLoaded;
    SatAppService service;
    ArrayList<Uri> fileUris = new ArrayList<Uri>();
    Activity act;
    RequestBody inventariableBody, titleBody, descriptionBody;

    public NewTicketDialogFragment(Context ctx, String invId) {
        this.ctx = ctx;
        this.inventariableId = invId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.new_ticket_dialog_title));
        builder.setMessage(getResources().getString(R.string.new_ticket_dialog_message));

        builder.setCancelable(true);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_ticket, null);
        builder.setView(v);

        act = (Activity) ctx;

        edTitle = v.findViewById(R.id.editTextTitleNewTicket);
        edDescription = v.findViewById(R.id.editTextDescriptionNewTicket);
        btnUploadFoto = v.findViewById(R.id.buttonLoadFotoOneNewTicket);
        ivFotoLoaded = v.findViewById(R.id.imageViewFotoOneNewTicket);

        btnUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });

        builder.setPositiveButton(getResources().getString(R.string.create), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do sign up
                String title = edTitle.getText().toString();
                String description = edDescription.getText().toString();

                if(title.isEmpty() || description.isEmpty()) {
                    if(title.isEmpty()) {
                        edTitle.setError(getResources().getString(R.string.new_ticket_dialog_error_need_title));
                    }
                    if(description.isEmpty()) {
                        edDescription.setError(getResources().getString(R.string.new_ticket_dialog_error_need_description));
                    }
                } else {
                    if (fileUris != null) {
                        try {

                            //TEXT PARTS
                            titleBody = RequestBody.create(MultipartBody.FORM, title);
                            descriptionBody = RequestBody.create(MultipartBody.FORM, description);
                            if(inventariableId != null){
                                inventariableBody = RequestBody.create(MultipartBody.FORM, inventariableId);
                            }

                            //LIST OF PARTS FOTOS
                            List<MultipartBody.Part> parts = new ArrayList<>();
                            for (int i = 0; i < fileUris.size() ; i++) {
                                InputStream inputStream = act.getContentResolver().openInputStream(fileUris.get(i));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                                int cantBytes;
                                byte[] buffer = new byte[1024*4];

                                while ((cantBytes = bufferedInputStream.read(buffer,0,1024*4)) != -1) {
                                    baos.write(buffer,0,cantBytes);
                                }

                                RequestBody requestFile =
                                        RequestBody.create(
                                                MediaType.parse(act.getContentResolver().getType(fileUris.get(i))), baos.toByteArray());

                                Cursor returnCursor = act.getContentResolver().query(fileUris.get(i), null, null, null, null);
                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                returnCursor.moveToFirst();
                                String fileName = returnCursor.getString(nameIndex);
                                MultipartBody.Part fotosbody =
                                        MultipartBody.Part.createFormData(Constants.FOTOS_NEW_TICKET_IMAGE_PART, fileName, requestFile);
                                parts.add(fotosbody);
                            }
                            //CALL TO API
                            service = SatAppServiceGenerator.createService(SatAppService.class);
                            if(inventariableId != null){
                                Call<TicketModel> callPostNewTicketInventariable = service.postNewTicketByInventariableId(parts, titleBody, descriptionBody, inventariableBody);
                                callPostNewTicketInventariable.enqueue(new Callback<TicketModel>() {
                                    @Override
                                    public void onResponse(Call<TicketModel> call, Response<TicketModel> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("Uploaded", "Éxito");
                                            Log.d("Uploaded", response.body().toString());
                                        } else {
                                            Log.e("Upload error", response.errorBody().toString());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<TicketModel> call, Throwable t) {
                                        Log.e("Upload error", t.getMessage());
                                    }
                                });
                            } else {
                                Call<TicketModel> callPostNewTicket = service.postNewTicket(parts, titleBody, descriptionBody);
                                callPostNewTicket.enqueue(new Callback<TicketModel>() {
                                    @Override
                                    public void onResponse(Call<TicketModel> call, Response<TicketModel> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("Uploaded", "Éxito");
                                            Log.d("Uploaded", response.body().toString());
                                        } else {
                                            Log.e("Upload error", response.errorBody().toString());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<TicketModel> call, Throwable t) {
                                        Log.e("Upload error", t.getMessage());
                                    }
                                });
                            }

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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

    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && resultData != null) {
            ClipData clipData = resultData.getClipData();
            fileUris.clear();
            if(clipData != null){
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    fileUris.add(uri);
                }
            } else {
                Uri uri = resultData.getData();
                fileUris.add(uri);
            }
            Glide
                    .with(this)
                    .load(fileUris.get(0))
                    .transform(new CircleCrop())
                    .into(ivFotoLoaded);
        }
    }

}
