package com.groupfive.satapp.ui.tickets.assignedtickets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Toast;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.data.viewModel.AssignedTicketsViewModel;
import com.groupfive.satapp.listeners.IAssignedTicketsListener;
import com.groupfive.satapp.models.calendar.CalendarModel;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.ui.datepicker.IDatePickerListener;
import com.groupfive.satapp.ui.tickets.ticketdetail.TicketDetailActivity;
import com.groupfive.satapp.ui.tickets.usertickets.ShowAllMyTicketsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShowAssignedTicketsActivity extends AppCompatActivity implements IAssignedTicketsListener, IDatePickerListener {

    List<TicketModel> assignedTickets = new ArrayList<>();
    AssignedTicketsViewModel assignedTicketsViewModel;
    //CALENDAR
    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    public static final int REQUEST_WRITE_CALENDAR = 78;
    Uri uri;
    private static final String DEBUG_TAG = "ActivityOfAssignedTicketsFragmentList";
    List<CalendarModel> calendarModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_assigned_tickets);
        assignedTicketsViewModel = new ViewModelProvider(this).get(AssignedTicketsViewModel.class);
        requestPermissionCalendar();
    }

    public void loadAssignedTickets(){
        assignedTicketsViewModel.getAssignedTickets().observe(this, new Observer<List<TicketModel>>() {
            @Override
            public void onChanged(List<TicketModel> list) {
                assignedTickets = list;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAssignedTickets();
    }

    private void requestPermissionCalendar() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR},
                    REQUEST_WRITE_CALENDAR);
        }
    }

    @Override
    public void onAssignedTicketItemClick(TicketModel ticketModel) {
        Intent i = new Intent(ShowAssignedTicketsActivity.this, TicketDetailActivity.class);
        i.putExtra(Constants.EXTRA_TICKET_ID, String.valueOf(ticketModel.getId()));
        startActivity(i);
    }

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    @Override
    public void onDateSelected(int year, int month, int day) {
        addTicketToCalendar(day, month+1, year);
    }

    public void addTicketToCalendar(int day, int month, int year) {
        Cursor cur = null;
        ContentResolver cr = this.getContentResolver();
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

        for (int i = 0; i <assignedTickets.size() ; i++) {
            ContentResolver contentResolver = this.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, assignedTickets.get(i).getTitulo());
            values.put(CalendarContract.Events.DESCRIPTION, assignedTickets.get(i).getDescripcion());
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Madrid/Spain");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR},
                        REQUEST_WRITE_CALENDAR);
            }
            Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
        }
        Toast.makeText(this, getResources().getString(R.string.calendar_all_events_added), Toast.LENGTH_SHORT).show();
    }
}
