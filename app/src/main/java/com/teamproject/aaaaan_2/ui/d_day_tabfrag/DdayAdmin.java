package com.teamproject.aaaaan_2.ui.d_day_tabfrag;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.data.Event;
import com.teamproject.aaaaan_2.data.ShareData;
import com.teamproject.aaaaan_2.dday.CalenderSet;
import com.teamproject.aaaaan_2.dday.DDayTool;
import com.teamproject.aaaaan_2.util.DialogSampleUtil;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class DdayAdmin extends Fragment {
    private final String TAG = "D-Day D-day_Admin";

    View view;

    Button buttonFinish;

    CheckBox armyDecrease;

    EditText editYear;
    EditText editMonth;
    EditText editDay;

    RadioGroup radioArmyGroup;

    RadioButton radioNavy;
    RadioButton radioAirForce;

    TextView inPutDate;
    TextView outPutDate;
    TextView textPercent;
    TextView textViewDDay;

    ProgressBar progress;

    double d_Check;
    int d_Firstday;

    DDayTool dDayTool;

    //입대일 덮어쓰기 여부
    boolean bExistInEvent=false;
    boolean bCheckUpdate=false;
    Event    updateInEvent,updateOutEvent;

    public DdayAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_date_input, container, false);

        buttonFinish = (Button) view.findViewById(R.id.buttonFinish);
        radioArmyGroup = (RadioGroup) view.findViewById(R.id.radioArmyGroup);
        editYear = (EditText) view.findViewById(R.id.editYear);
        editMonth = (EditText) view.findViewById(R.id.editMonth);
        editDay = (EditText) view.findViewById(R.id.editDay);
        armyDecrease = (CheckBox) view.findViewById(R.id.armyDecrease);
        radioNavy = (RadioButton) view.findViewById(R.id.radioNavy);
        radioAirForce = (RadioButton) view.findViewById(R.id.radioAirForce);
        inPutDate = (TextView) view.findViewById(R.id.inPutDate);
        outPutDate = view.findViewById(R.id.outPutDate);
        textPercent = (TextView) view.findViewById(R.id.textPercent);
        progress = (ProgressBar) view.findViewById(R.id.progressArmy);
        textViewDDay = (TextView) view.findViewById(R.id.dDay);

        //완료버튼 눌렀을때
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //기존입대일 존재여부 체크
                for(final Event event:ShareData.mEventList){

                    if(event.getmTitle().equals("입대일")) {
                        bExistInEvent = true;
                        updateInEvent = event;
                        break;
                    }
                }

                if(bExistInEvent){
                    bCheckUpdate=true;
                    DialogSampleUtil.showConfirmDialog(getContext(),"","이미 정보가 존재합니다. \n 덮어 쓰시겠습니까?",new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what==0)return;

                            in_out_process();
                            save();

                        }
                    });
                } else {
                    bCheckUpdate=false;
                    in_out_process();
                    save();
                }
            }
        });

        return view;
    }

    // 각 EditText에 입력된걸 검사하고 계산하여 아래에 결과를 뿌려준다.
    public void in_out_process(){

        Log.d("MY","--------------" + bCheckUpdate);

        String armyName;
        boolean isArmyDecrease = armyDecrease.isChecked();

        String strYear = editYear.getText().toString();
        if (strYear.isEmpty()) {
            Toast.makeText(getContext(), "년도를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        String strMonth = editMonth.getText().toString();
        if (strMonth.isEmpty()) {
            Toast.makeText(getContext(), "월을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        String strDay = editDay.getText().toString();
        if (strDay.isEmpty()) {
            Toast.makeText(getContext(), "일을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        int year = Integer.parseInt(strYear);
        int month = Integer.parseInt(strMonth);
        int day = Integer.parseInt(strDay);

        if (radioArmyGroup.getCheckedRadioButtonId() == R.id.radioNavy) {
            armyName = "해군";
        } else if (radioArmyGroup.getCheckedRadioButtonId() == R.id.radioAirForce) {
            armyName = "공군";
        } else {
            armyName = "육군";
        }

        //입대일
        String strInputDate = String.format("%d-%02d-%02d", year, month, day);
        inPutDate.setText(strInputDate);

        //입대일 DB 등록(CalendarViewWithNotesActivitySDK21에 저장)
        //시간을 id로 사용 (타임스탬프 개념)
        String id = System.currentTimeMillis() + "";
        Calendar in_date = Calendar.getInstance();
        in_date.set(year, month - 1, day);

        if(bCheckUpdate){
            updateInEvent.setmDate(in_date);
        }else {
            Event myevent = new Event(id, "입대일", in_date, Color.RED, true);
            ShareData.mEventList.add(myevent);
        }

        CalenderSet calenderSet = new CalenderSet();

        //전역일 계산
        int[] result_date = calenderSet.outPutDate(year, month, day, armyName, isArmyDecrease);
        int outYear = result_date[0];
        int outMonth = result_date[1];
        int outDay = result_date[2];

        String strOutputDate = String.format("%d-%02d-%02d", outYear, outMonth, outDay);
        outPutDate.setText(strOutputDate);

        //전역일 DB 등록
        id = System.currentTimeMillis() + "";
        Calendar out_date = Calendar.getInstance();
        out_date.set(outYear, outMonth - 1, outDay);

        if(bCheckUpdate){
            //전역일 객체구하기
            for(final Event event2:ShareData.mEventList){
                if(event2.getmTitle().equals("전역일")){
                    updateOutEvent = event2;
                    break;
                }
            }
            updateOutEvent.setmDate(out_date);

        }else {
            Event myevent1 = new Event(id, "전역일", out_date, Color.RED, true);
            ShareData.mEventList.add(myevent1);
        }


        //입력받은 날짜로 전역일 /D-1 계산후 출력

        dDayTool = new DDayTool();

        Calendar today = Calendar.getInstance();
        // tyear, tmonth, tday = 오늘 년월일
        int tYear = today.get(Calendar.YEAR);
        int tMonth = today.get(Calendar.MONTH) + 1;
        int tDay = today.get(Calendar.DATE);
        //오늘->전역일까지날수
        int d_Remainday = dDayTool.FirstDay(tYear, tMonth, tDay, outYear, outMonth, outDay);
        //입대일->전역일까지날수
        d_Firstday = dDayTool.FirstDay(year, month, day, outYear, outMonth, outDay);
        //DDayTool에 있는 진행도계산 메소드
        d_Check = dDayTool.Check(d_Remainday, d_Firstday);

        textPercent.setText((int) d_Check + "%");

        progress.setProgress((int) d_Check);
        // Date_input.this.finish();
        textViewDDay.setText("D-" + d_Remainday);

        //키패드 내리기
        DialogSampleUtil.hideKeypad(getContext(), editYear);

        bCheckUpdate=false;
    }

    // db에 저장.
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
            //Toast.makeText(getContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, se.getMessage());
        }
    }
}