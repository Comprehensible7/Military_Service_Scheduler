package com.teamproject.aaaaan_2.ui.menu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.data.Event;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by hanman-yong on 2020-01-06.
 */
public class FirstScreenFragment extends Fragment {
    private final String TAG = "D-Day FirstFrag";

    Event event;

    TextView enlistment_day;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_first_screen_fragment, container, false);

        enlistment_day = (TextView) rootView.findViewById(R.id.tv_enlistment_day);

        select_open();

        try {
            if(event != null) {
                //D-day계산식----------------------------------------
                Calendar today = Calendar.getInstance();                    //현재 날짜 불러옴

                long t = today.getTimeInMillis() / 86400000;                 //오늘 날짜를 밀리타임으로 바꿈
                long d = event.getDate().getTimeInMillis() / 86400000;       //디데이날짜를 밀리타임으로 바꿈
                int day_r = (int) (d - t);                                    //디데이 날짜에서 오늘 날짜를 뺀 값을 '일'단위로 바꿈

                String strDay = "";

                if (day_r > 0) {
                    strDay = String.format("전역까지 %d일 남았습니다.", day_r);
                } else if (day_r == 0) {
                    strDay = String.format("전역을 축하합니다 ^^");
                } else if (day_r < 0) {
                    strDay = String.format("전역한지 %d일 되었습니다.", -day_r);
                }

                enlistment_day.setText(strDay);

            } else {
                enlistment_day.setText("입대일을 입력해주세요.");
            }
        } catch (Exception e){
            Log.d(TAG, String.format(e.getMessage()));

        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        event = null;
    }

    protected void select_open() {

        try {
            String dbName = "eventDB";
            String tableName = "eventCal";

            SQLiteDatabase ReadDB = getContext().openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            //SELECT문을 사용하여 테이블에 있는 데이터를 가져온다.
            Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName, null);

            if(c!=null && c.moveToFirst()) {
                while (c.moveToNext()) {
                    String title = c.getString(c.getColumnIndex("title"));
                    Log.d(TAG, title);
                    if (title.equals("전역일")) {

                        String id = c.getString(c.getColumnIndex("id"));

                        int year = c.getInt(c.getColumnIndex("year"));
                        int month = c.getInt(c.getColumnIndex("month"));
                        int day = c.getInt(c.getColumnIndex("date"));
                        int color = c.getInt(c.getColumnIndex("color"));

                        Calendar cc = Calendar.getInstance(); // ? 중복
                        cc.set(year, month - 1, day);

                        event = new Event(id, title, cc, color, true);
                    }
                }
            }

            ReadDB.close();

        } catch (SQLiteException se) {
            Toast.makeText(getContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, se.getMessage());
        }
    }
}
