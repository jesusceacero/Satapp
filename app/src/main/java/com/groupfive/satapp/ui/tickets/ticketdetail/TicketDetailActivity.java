package com.groupfive.satapp.ui.tickets.ticketdetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.commons.SharedPreferencesManager;
import com.groupfive.satapp.data.viewModel.TicketByIdViewModel;
import com.groupfive.satapp.listeners.OnNewTicketDialogListener;
import com.groupfive.satapp.models.calendar.CalendarModel;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;
import com.groupfive.satapp.transformations.DateTransformation;
import com.groupfive.satapp.ui.annotations.allticketannotation.ShowAllTicketAnnotationsActivity;
import com.groupfive.satapp.ui.annotations.newannotation.NewAnnotationDialogFragment;
import com.groupfive.satapp.ui.datepicker.DialogDatePickerFragment;
import com.groupfive.satapp.ui.datepicker.IDatePickerListener;
import com.groupfive.satapp.ui.tickets.addtechnician.AddThechnicianShowActivity;
import com.groupfive.satapp.ui.tickets.changestate.ChangeStateTicketActivity;
import com.groupfive.satapp.ui.tickets.phototicketdetail.ShowPhotosTicektActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketDetailActivity extends AppCompatActivity implements OnNewTicketDialogListener, IDatePickerListener {

    DateTransformation dateTransformer = new DateTransformation();
    String ticketId, userRole;
    TicketByIdViewModel ticketByIdViewModel;
    ImageView ivToolbar;
    SatAppService service;
    TextView txtTitle, txtCreatedByName, txtEmailCreatedBy, txtDate, txtState, txtDescription;
    Button btnImgs;
    TicketModel ticketDetail;
    FloatingActionButton fab;
    File ticketPhoto;
    //ProgressBar progressBar;
    //CALENDAR
    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    public static final int REQUEST_READ_CALENDAR = 79;
    public static final int REQUEST_WRITE_CALENDAR = 78;
    Uri uri;
    private static final String DEBUG_TAG = "TicektDetailActivity";
    List<CalendarModel> calendarModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        ivToolbar = findViewById(R.id.imageViewDetailTicketToolbar);
        txtTitle = findViewById(R.id.textViewTitleTicketDetail);
        txtCreatedByName = findViewById(R.id.textViewCreatedByTicketDetail);
        txtEmailCreatedBy = findViewById(R.id.textViewCreatedByEmailTicektDetail);
        txtDate = findViewById(R.id.textViewDateTicketDetail);
        txtState = findViewById(R.id.textViewStateTicketDetail);
        txtDescription = findViewById(R.id.textViewDescriptionTicketDetail);
        btnImgs = findViewById(R.id.buttonFotosTicektDetail);
        // progressBar = findViewById(R.id.progressBarTicketDetail);

        ticketId = getIntent().getExtras().get(Constants.EXTRA_TICKET_ID).toString();
        SharedPreferencesManager.setStringValue(Constants.SHARED_PREFERENCES_TICKET_ID, ticketId);
        userRole = MyApp.getContext().getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE).getString(Constants.SHARED_PREFERENCES_ROLE, null);

        requestPermissionCalendar();

        ticketByIdViewModel = new ViewModelProvider(this).get(TicketByIdViewModel.class);
        ticketByIdViewModel.setTicketId(ticketId);
        service = SatAppServiceGenerator.createService(SatAppService.class);

        loadTicket();

        fab = (FloatingActionButton) findViewById(R.id.fabEditTicketDetail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EdtiTicketDialogFragment dialog = new EdtiTicketDialogFragment(TicketDetailActivity.this, ticketId, TicketDetailActivity.this);
                dialog.show(getSupportFragmentManager(), "EdtiTicketDialogFragment");
            }
        });


        btnImgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFotos = new Intent(TicketDetailActivity.this, ShowPhotosTicektActivity.class);
                startActivity(intentFotos);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTicket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        userRole = getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE).getString(Constants.SHARED_PREFERENCES_ROLE, null);
        if(userRole.equals(Constants.ROLE_USER)){
            getMenuInflater().inflate(R.menu.user_menu_ticket_detail, menu);
        } else if(userRole.equals(Constants.ROLE_ADMIN)){
            getMenuInflater().inflate(R.menu.admin_menu_ticket_detail, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_ticket_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_ticket:
                AlertDialog.Builder alert = new AlertDialog.Builder(TicketDetailActivity.this);
                alert.setTitle(getResources().getString(R.string.delete_ticket_title));
                alert.setMessage(getResources().getString(R.string.delete_ticket_message));
                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<ResponseBody> call = service.deleteTicketById(ticketId);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.code() == 204) {
                                    Toast.makeText(TicketDetailActivity.this, getResources().getString(R.string.ticket_deleted), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(TicketDetailActivity.this, getResources().getString(R.string.error_ticket_deleted), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
            case R.id.action_share_ticket:
                shareTicket();
                return true;
            case R.id.action_add_thecnical:
                if(userRole.equals(Constants.ROLE_ADMIN) || userRole.equals(Constants.ROLE_TECNICO)){
                    Intent addTechnicanl = new Intent(TicketDetailActivity.this, AddThechnicianShowActivity.class);
                    addTechnicanl.putExtra(Constants.EXTRA_TICKET_ID, String.valueOf(ticketId));
                    startActivity(addTechnicanl);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.acces_denied_by_role), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_change_state:
                Intent changeState = new Intent(TicketDetailActivity.this, ChangeStateTicketActivity.class);
                changeState.putExtra(Constants.EXTRA_TICKET_ID, String.valueOf(ticketId));
                startActivity(changeState);
                return true;
            case R.id.action_add_anotation:
                NewAnnotationDialogFragment dialog = new NewAnnotationDialogFragment(TicketDetailActivity.this, ticketId);
                dialog.show(getSupportFragmentManager(), "NewAnnotationDialogFragment");
                return true;
            case R.id.action_all_anotations:
                Intent intentAnnotation = new Intent(TicketDetailActivity.this, ShowAllTicketAnnotationsActivity.class);
                startActivity(intentAnnotation);
                return true;
            case R.id.action_add_to_calendar:
                DialogFragment datePickerFragment = DialogDatePickerFragment.newInstance(this);
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadTicket() {
        ticketByIdViewModel.getTicketById().observe(this, new Observer<TicketModel>() {
            @Override
            public void onChanged(TicketModel ticketModel) {
                ticketDetail = ticketModel;
                String string = ticketModel.getFotos().get(0);
                String[] parts = string.split("/");
                String part1 = parts[3];
                String part2 = parts[4];
                Call<ResponseBody> call = service.getTicketImg(part1, part2);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                                Glide
                                        .with(TicketDetailActivity.this)
                                        .load(bmp)
                                        .error(Glide.with(TicketDetailActivity.this).load(R.drawable.image_not_loaded_icon))
                                        .thumbnail(Glide.with(TicketDetailActivity.this).load(R.drawable.loading_gif))
                                        .into(ivToolbar);
                            }
                        } else {
                            Glide
                                    .with(TicketDetailActivity.this)
                                    .load(R.drawable.image_not_loaded_icon)
                                    .error(Glide.with(TicketDetailActivity.this).load(R.drawable.image_not_loaded_icon))
                                    .thumbnail(Glide.with(TicketDetailActivity.this).load(R.drawable.loading_gif))
                                    .into(ivToolbar);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(TicketDetailActivity.this, getResources().getString(R.string.error_loading_ticket), Toast.LENGTH_SHORT).show();
                    }
                });
                txtTitle.setText(ticketModel.getTitulo());
                txtCreatedByName.setText(getResources().getString(R.string.name_created_by) + " " + ticketModel.getCreadoPor().getName());
                txtEmailCreatedBy.setText(getResources().getString(R.string.contact_created_by) + " " + ticketModel.getCreadoPor().getEmail());
                String stringDate = ticketModel.getFechaCreacion();
                String[] partsDate = stringDate.split("T");
                String date = partsDate[0];
                String dateToShow = dateTransformer.dateTransformation(date);
                txtDate.setText(dateToShow);
                if(ticketModel.getEstado().equals("PENDIENTE_ASIGNACION")){
                    txtState.setText(getResources().getString(R.string.state) + " " + getResources().getString(R.string.btn_state_pendiente));
                } else if(ticketModel.getEstado().equals("ASIGNADA")){
                    txtState.setText(getResources().getString(R.string.state) + " " + getResources().getString(R.string.btn_asignada));
                } else if(ticketModel.getEstado().equals("EN_PROCESO")){
                    txtState.setText(getResources().getString(R.string.state) + " " + getResources().getString(R.string.btn_en_proceso));
                } else if(ticketModel.getEstado().equals("SOLUCIONADA")){
                    txtState.setText(getResources().getString(R.string.state) + " " + getResources().getString(R.string.btn_solucionada));
                }
                txtDescription.setText(ticketModel.getDescripcion());
                //progressBar.setVisibility(View.INVISIBLE);
                ivToolbar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtCreatedByName.setVisibility(View.VISIBLE);
                txtEmailCreatedBy.setVisibility(View.VISIBLE);
                txtDate.setVisibility(View.VISIBLE);
                txtState.setVisibility(View.VISIBLE);
                txtDescription.setVisibility(View.VISIBLE);
                btnImgs.setVisibility(View.VISIBLE);
                fab.show();
                //progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void requestPermissionCalendar() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(TicketDetailActivity.this, Manifest.permission.WRITE_CALENDAR)) {
        } else {
            ActivityCompat.requestPermissions(TicketDetailActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR},
                    REQUEST_WRITE_CALENDAR);
        }
    }

    @Override
    public void onTicketUpdated() {
        loadTicket();
    }

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };


    public void addTicketToCalendar(int day, int month, int year) {
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String userEmail = MyApp.getContext().getSharedPreferences(Constants.APP_SETTINGS_FILE, Context.MODE_PRIVATE).getString(Constants.SHARED_PREFERENCES_EMAIL, null);
        String[] selectionArgs = new String[]{userEmail, "com.google", userEmail};
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            calendarModelList.add(new CalendarModel(calID, displayName, accountName, ownerName));
        }
        long calID = calendarModelList.get(0).getCalID();
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day);// ,0 ,0
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day);
        endMillis = endTime.getTimeInMillis();

        ContentResolver contentResolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, ticketDetail.getTitulo());
        values.put(CalendarContract.Events.DESCRIPTION, ticketDetail.getDescripcion());
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Madrid/Spain");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TicketDetailActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR},
                    REQUEST_WRITE_CALENDAR);
        }
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);

        Toast.makeText(this, getResources().getString(R.string.calendar_event_added), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSelected(int year, int month, int day) {
        addTicketToCalendar(day, month+1, year);
    }

    public void shareTicket(){
        String string = ticketDetail.getFotos().get(0);
        String[] parts = string.split("/");
        String part1 = parts[3];
        String part2 = parts[4];
        Call<ResponseBody> call = service.getTicketImg(part1, part2);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // Obtenemos, a partir de las cabeceras, el tipo mime, y de él, la extensión
                        Headers headers = response.headers();
                        String contentType = headers.get("Content-Type");
                        String suffix = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType);

                        // "Armamos" lo necesario para leer el fichero vía flujos
                        InputStream is = response.body().byteStream();
                        // Se guarda en un fichero temporal, dentro de la caché externa
                        try {
                            ticketPhoto = File.createTempFile("img", suffix, getExternalCacheDir());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        BufferedInputStream input = new BufferedInputStream(is);
                        BufferedOutputStream output = null;
                        try {
                            output = new BufferedOutputStream(new FileOutputStream(ticketPhoto));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        // Bucle clásico de lectura de un fichero en grupos de 4KB
                        byte[] data = new byte[4*1024];

                        long total = 0;
                        int count = 0;
                        while (true) {
                            try {
                                if (!((count = input.read(data)) != -1)) break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            total += count;
                            try {
                                output.write(data, 0, count);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // Cierre de flujos
                        try {
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Uri myPhotoFileUri = FileProvider.getUriForFile(TicketDetailActivity.this, TicketDetailActivity.this.getApplicationContext().getPackageName() + ".provider", ticketPhoto);

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.putExtra(Intent.EXTRA_TEXT,"Ticket: " + ticketDetail.getTitulo() + "\n " + getResources().getString(R.string.share_ticket_content) + " " + ticketDetail.getDescripcion());
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.shared_ticket) + " " + ticketDetail.getTitulo());
                        shareIntent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri);

                        String type = null;
                        String extension = MimeTypeMap.getFileExtensionFromUrl(ticketPhoto.getAbsolutePath());
                        if (extension != null) {
                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        }
                        shareIntent.setType(type);
                        startActivity(Intent.createChooser(shareIntent, "Enviar"));
                    } else {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.shared_ticket) + " " + ticketDetail.getTitulo());
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Ticket: " + ticketDetail.getTitulo() + "\n " + getResources().getString(R.string.share_ticket_content) + " " + ticketDetail.getDescripcion());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, getResources().getString(R.string.share));
                        startActivity(shareIntent);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TicketDetailActivity.this, getResources().getString(R.string.error_loading_ticket), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
