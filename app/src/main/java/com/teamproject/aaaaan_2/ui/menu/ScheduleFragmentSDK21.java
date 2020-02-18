package com.teamproject.aaaaan_2.ui.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamproject.aaaaan_2.calendar.CreateEventActivity;
import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.data.Event;
import com.teamproject.aaaaan_2.data.ShareData;
import com.teamproject.aaaaan_2.uihelpers.CalendarDialog;
import com.teamproject.calendarviewlib.CalendarView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;



@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScheduleFragmentSDK21 extends Fragment {

    private final static int CREATE_EVENT_REQUEST_CODE = 100;

    private String[] mShortMonths;
    private CalendarView mCalendarView;
    private CalendarDialog mCalendarDialog;

    LayoutInflater layoutInflater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Open();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.layoutInflater = inflater;
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_schedule_sdk21, container, false);

        Log.d("MY","---SDK21---");

        mShortMonths = new DateFormatSymbols().getShortMonths();

        Toolbar toolbar = rootView.findViewById(R.id.schedule_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mCalendarView = rootView.findViewById(R.id.calendarView);
        mCalendarView.setOnMonthChangedListener(new CalendarView.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(int month, int year) {
                if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mShortMonths[month]);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(Integer.toString(year));
                }
            }
        });
        mCalendarView.setOnItemClickedListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClicked(List<CalendarView.CalendarObject> calendarObjects,
                                      Calendar previousDate,
                                      Calendar selectedDate) {
                if (calendarObjects.size() != 0) {
                    mCalendarDialog.setSelectedDate(selectedDate);
                    mCalendarDialog.show();
                }
                else {
                    if (diffYMD(previousDate, selectedDate) == 0)
                        createEvent(selectedDate);
                }
            }
        });

        for (Event e : ShareData.mEventList) {
            mCalendarView.addCalendarObject(parseCalendarObject(e));
        }

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            int month = mCalendarView.getCurrentDate().get(Calendar.MONTH);
            int year = mCalendarView.getCurrentDate().get(Calendar.YEAR);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mShortMonths[month]);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(Integer.toString(year));
        }

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent(mCalendarView.getSelectedDate());
            }
        });

        mCalendarDialog = CalendarDialog.Builder.instance(getContext())
                .setEventList(ShareData.mEventList)
//                .setOnItemClickListener(new CalendarDialog.OnCalendarDialogListener() {
//                    @Override
//                    public void onEventClick(Event event) {
//                        onEventSelected(event);
//                    }
//
//                    @Override
//                    public void onCreateEvent(Calendar calendar) {
//                        //createEvent(calendar);
//                    }
//                })
                .create();


        //ShareData.cv21 = this;
        if(ShareData.bSave) {
            addCalendar();
            ShareData.bSave=false;
        }

        //Open();

        return rootView;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getLayoutInflater().inflate(R.menu.menu_toolbar_calendar_view, (ViewGroup) menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_today: {
                mCalendarView.setSelectedDate(Calendar.getInstance());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onEventSelected(Event event) {
        Activity context = getActivity();
        Intent intent = CreateEventActivity.makeIntent(context, event);

        startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        //overridePendingTransition( R.anim.slide_in_up, R.anim.stay );
    }

    private void createEvent(Calendar selectedDate) {
        Activity context = getActivity();
        Intent intent = CreateEventActivity.makeIntent(context, selectedDate);

        startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        //overridePendingTransition( R.anim.slide_in_up, R.anim.stay );
    }


    // GeneralDday의 결과값을 가져와서 캘린더에 표현해준다
    public void addCalendar(){

        // Calendar mycal = Calendar.getInstance();
        //mycal.set(2020,0,13);

        Event myevent = new Event(ShareData.id,ShareData.title, ShareData.calendar,ShareData.color,true);

        ShareData.mEventList.add(myevent);
        mCalendarView.addCalendarObject(parseCalendarObject(myevent));
        mCalendarDialog.setEventList(ShareData.mEventList);
    }


    public static int diffYMD(Calendar date1, Calendar date2) {
        if (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
                date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH))
            return 0;

        return date1.before(date2) ? -1 : 1;
    }

    private static CalendarView.CalendarObject parseCalendarObject(Event event) {
        return new CalendarView.CalendarObject(
                event.getID(),
                event.getTitle(),
                event.getDate(),
                event.getColor(),
                event.isCompleted() ? Color.TRANSPARENT : Color.RED);
    }


    // mEventList에 저장된 데이터를 호출한다
    protected void Open() {

        ShareData.mEventList.clear();

        try {
            String dbName = "eventDB";
            String tableName = "eventCal";

            SQLiteDatabase ReadDB = getContext().openOrCreateDatabase(dbName, MODE_PRIVATE, null);


            //SELECT문을 사용하여 테이블에 있는 데이터를 가져온다..
            Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName, null);

            if (c != null) {


                if (c.moveToFirst()) {
                    do {

                        //테이블에서 두개의 컬럼값을 가져와서
                        String id = c.getString(c.getColumnIndex("id"));
                        String title = c.getString(c.getColumnIndex("title"));

                        int year = c.getInt(c.getColumnIndex("year"));
                        int month = c.getInt(c.getColumnIndex("month"));
                        int day = c.getInt(c.getColumnIndex("date"));
                        int color = c.getInt(c.getColumnIndex("color"));

                        Calendar cc = Calendar.getInstance(); // ? 중복
                        cc.set(year, month - 1, day);

                        Event event = new Event(id, title, cc, color, true);


                        Log.d("MY","--select title : "+ title);
                        Log.d("MY","--select id : "+ id);


                        //ArrayList에 추가..
                        ShareData.mEventList.add(event);

                    } while (c.moveToNext());
                }
            }

            ReadDB.close();


        } catch (SQLiteException se) {
            Toast.makeText(getContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("", se.getMessage());
        }
    }

    // SQLite를 사용하여 데이터를 mEventList에 저장한다
    public void save(){
        String dbName = "eventDB";
        String tableName = "eventCal";

        SQLiteDatabase eventDB = null;

        try {

            eventDB = getContext().openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            //테이블이 존재하지 않으면 새로 생성..
            eventDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
                    + " ( id text primary key,title text,year int,month int ,date int,color int);");


            eventDB.execSQL("DELETE FROM " + tableName);
            //새로운 데이터를 테이블에 집어넣는다..
            for (int i = 0; i < ShareData.mEventList.size(); i++) {
                Event event = ShareData.mEventList.get(i);
                int year = event.getDate().get(Calendar.YEAR);
                int month = event.getDate().get(Calendar.MONTH) + 1;
                int day = event.getDate().get(Calendar.DATE);
                eventDB.execSQL("INSERT INTO " + tableName
                        + " (id,title,year,month,date,color)  Values ('"
                        + event.getID() + "', '" + event.getTitle() + "',"
                        + year + "," + month + "," + day + "," + event.getColor() + ");"
                );

                Log.d("MY","--insert--");

            }

            eventDB.close();

        } catch (SQLiteException se) {
            Toast.makeText(getContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("", se.getMessage());
        }



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_EVENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int action = CreateEventActivity.extractActionFromIntent(data);
                Event event = CreateEventActivity.extractEventFromIntent(data);


                switch (action) {
                    case CreateEventActivity.ACTION_CREATE: {

                        ShareData.mEventList.add(event);
                        mCalendarView.addCalendarObject(parseCalendarObject(event));
                        mCalendarDialog.setEventList(ShareData.mEventList);
                        save();
                        //  Log.d("MY","--save()--");
                        break;
                    }
                    case CreateEventActivity.ACTION_EDIT: {
                        Event oldEvent = null;
                        for (Event e : ShareData.mEventList) {
                            if (Objects.equals(event.getID(), e.getID())) {
                                oldEvent = e;
                                break;
                            }
                        }
                        if (oldEvent != null) {
                            ShareData.mEventList.remove(oldEvent);
                            ShareData.mEventList.add(event);

                            mCalendarView.removeCalendarObjectByID(parseCalendarObject(oldEvent));
                            mCalendarView.addCalendarObject(parseCalendarObject(event));
                            mCalendarDialog.setEventList(ShareData.mEventList);
                        }
                        break;
                    }
                    case CreateEventActivity.ACTION_DELETE: {
                        Event oldEvent = null;
                        for (Event e : ShareData.mEventList) {
                            if (Objects.equals(event.getID(), e.getID())) {
                                oldEvent = e;
                                break;
                            }
                        }
                        if (oldEvent != null) {
                            ShareData.mEventList.remove(oldEvent);
                            mCalendarView.removeCalendarObjectByID(parseCalendarObject(oldEvent));

                            mCalendarDialog.setEventList(ShareData.mEventList);
                            save();
                        }
                        break;
                    }
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}