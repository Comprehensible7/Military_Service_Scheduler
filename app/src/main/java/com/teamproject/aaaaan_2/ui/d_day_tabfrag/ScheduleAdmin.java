package com.teamproject.aaaaan_2.ui.d_day_tabfrag;
//---------일반디데이 입출력

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.data.Event;
import com.teamproject.aaaaan_2.data.ShareData;
import com.teamproject.aaaaan_2.util.DialogSampleUtil;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


public class ScheduleAdmin extends Fragment {

    private final String TAG = "D-Day ScheduleAdmin";

    View view;
    LayoutInflater inflater;

    private TextView todayText;

    private EditText eresultText;

    private Button bt_date;

    private int tYear;               //오늘 년월일 변수
    private int tMonth;
    private int tDay;

    private int dYear = 1;           //디데이 년월일 변수
    private int dMonth = 1;
    private int dDay = 1;


    private long d;                   //디데이날짜를 밀리타임으로 바꿈
    private long t;                   //오늘 날짜를 밀리타임으로 바꿈
    private long r;                   //디데이 날짜에서 오늘 날짜를 뺀 값을 '일'단위로 바꿈

    private int resultNumber = 0;

    static final int DATE_DIALOG_ID = 0;

    EditText et_title;
    EditText et_year;
    EditText et_month;
    EditText et_day;

    //리스트뷰
    ListView lv_event_list;

    public ScheduleAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        view = inflater.inflate(R.layout.fragment_general_d_day, container, false);
        todayText = (TextView) view.findViewById(R.id.today);

        eresultText = (EditText) view.findViewById(R.id.editresult);

        bt_date = (Button) view.findViewById(R.id.bt_date);

        //리스트뷰 참조값
        lv_event_list = view.findViewById(R.id.lv_evnet_list);

        Calendar calendar = Calendar.getInstance();              //현재 날짜 불러옴
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);


        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear, dMonth, dDay);

        t = calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈

        // 버튼 클릭 시 달력선택 팝업창 출력
        bt_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                onCreateDialog(0);//----------------
                DatePickerDialog dialog = new DatePickerDialog(getContext(), dDateSetListener, tYear, tMonth, tDay);
                dialog.show();
                Toast.makeText(getContext(), "날짜를 선택하세요!", Toast.LENGTH_SHORT).show();
            }
        });

        updateDisplay();

        return view;
    }


    //리스트뷰 배치 어텝터
    class EventListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ShareData.mEventList == null ? 0 : ShareData.mEventList.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView tv = null;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.dday_list_item, null);

            }
            //inflate된 뷰(convertView) 내에 있는 TextView의 참조값 얻어오기
            TextView tv_general_list = view.findViewById(R.id.tv_general_list);
            final Button bt_delete = view.findViewById(R.id.bt_delete);
            final Button bt_modify = view.findViewById(R.id.bt_modify);


            // Year,Month,Day, mEventList의 정보를 가져온다
            final Event event = ShareData.mEventList.get(i);
            int year = event.getDate().get(Calendar.YEAR);
            int month = event.getDate().get(Calendar.MONTH) + 1;
            int day = event.getDate().get(Calendar.DATE);
            String strDate = String.format("%d년 %d월 %d일", year, month, day);

            //D-day계산식----------------------------------------
            Calendar today = Calendar.getInstance();                    //현재 날짜 불러옴
            long t = today.getTimeInMillis() / 86400000;                 //오늘 날짜를 밀리타임으로 바꿈
            long d = event.getDate().getTimeInMillis() / 86400000;       //디데이날짜를 밀리타임으로 바꿈
            int day_r = (int) (d - t);                                    //디데이 날짜에서 오늘 날짜를 뺀 값을 '일'단위로 바꿈

            String strDay = "";
            //디데이 날짜가 오늘날짜보다 뒤에오면 '-', 앞에오면 '+'를 붙임

            // Year,Month,Day 디데이 출력부분 (ListView에 출력됨)
            if (day_r > 0) {
                strDay = String.format("D-%d", day_r);
            } else if (day_r == 0) {
                strDay = String.format("D-day");
            } else if (day_r < 0) {
                strDay = String.format("D+%d", -day_r);
            }

            String str = String.format("제목:%s\n날짜:%s\n%s\n", event.getTitle(), strDate, strDay);
            tv_general_list.setText(str);

            // 수정버튼 클릭
            bt_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCreateDialog(event);
                    Log.d("MY", "--G_Modifying--");
                }
            });

            // 삭제버튼 클릭
            bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {//Yes
                                ShareData.mEventList.remove(event);
                                save();
                                //배치 어텝터 설정
                                lv_event_list.setAdapter(new ScheduleAdmin.EventListAdapter());
                            }
                        }
                    };

                    DialogSampleUtil.showConfirmDialog(getContext(), "", "삭제하시겠습니까?", handler);

                    Log.d("MY", "--G_Delete--");

                }
            });

            return view;
        }


        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void onCreateDialog(Event event) {
            Log.d(TAG, " 됐음? ");

            // 레이아웃 XML파일을 View객체로 만들기 위해 inflater 사용
            View view = getLayoutInflater().inflate(R.layout.fragment_modify_general, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            et_title = (EditText) view.findViewById(R.id.modi_title);
            et_year = (EditText) view.findViewById(R.id.modi_year);
            et_month = (EditText) view.findViewById(R.id.modi_month);
            et_day = (EditText) view.findViewById(R.id.modi_day);

            int year = event.getmDate().get(Calendar.YEAR);
            int month = event.getmDate().get(Calendar.MONTH) + 1;
            int day = event.getmDate().get(Calendar.DATE);

            // Title,YMD 정보를 넣어줌
            et_title.setText(event.getmTitle());
            et_year.setText("" + year);
            et_month.setText("" + month);
            et_day.setText("" + day);

            builder.setView(view)
                    .setPositiveButton("변경하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String title = et_title.getText().toString();
                            String m_year = et_year.getText().toString();
                            String m_month = et_month.getText().toString();
                            String m_day = et_day.getText().toString();


                            int year = Integer.parseInt(m_year);
                            int month = Integer.parseInt(m_month) - 1;
                            int day = Integer.parseInt(m_day);

                            //ID가 똑같은 값 찾아서 변경해줌
                            for(int i=0;i<ShareData.mEventList.size();i++){
                                Event e = ShareData.mEventList.get(i);
                                if(e.getmID().equals(event.getmID())){

                                    e.setmTitle(title);
                                    e.getDate().set(year,month,day);

                                    break;
                                }
                            }

                            save();

                            Toast.makeText(getActivity(), "수정 하였습니다.", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "취소.", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    }).show();
        }
    }



    // 날짜 값 받아오기
    private void updateDisplay() {

        todayText.setText(String.format("%d년 %d월 %d일", tYear, tMonth + 1, tDay));

    }

    private DatePickerDialog.OnDateSetListener dDateSetListener = new DatePickerDialog.OnDateSetListener() {

        // 날짜를 선택할 수 있도록 뷰를 생성해서 보여준다
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            dYear = year;
            dMonth = monthOfYear;

            // Log.d("MY",dMonth+"");

            dDay = dayOfMonth;
            final Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(dYear, dMonth, dDay);

            d = dCalendar.getTimeInMillis();
            r = (d - t) / (24 * 60 * 60 * 1000) + 1;

            resultNumber = (int) r;
            //updateDisplay();


            dCalendar.set(dYear, dMonth, dDay);

            // 시간을 id로 사용 (타임스탬프 개념으로)
            long nid = System.currentTimeMillis();

            // 이벤트 리스트에 내용을 추가
            Event event = new Event(nid + "", eresultText.getText().toString(), dCalendar, Color.BLUE, false);
            ShareData.mEventList.add(event);

            // 이벤트 리스트 입력갯수 확인하기
            Log.d("MY", "mEventList's size=" + ShareData.mEventList.size() + "");
            save();

            // 날짜 선택 후 등록완료 메세지 출력
            Toast.makeText(getContext(), "등록되었습니다!", Toast.LENGTH_SHORT).show();

            eresultText.setText("");
            //키패드 내리기
            DialogSampleUtil.hideKeypad(getContext(), eresultText);

        }
    };

    // DB에 내용저장
    public void save() {
        String dbName = "eventDB";
        String tableName = "eventCal";

        SQLiteDatabase eventDB = null;

        try {

            eventDB = getContext().openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            //테이블이 존재하지 않으면 새로 생성
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

                Log.d("MY", "--insert--");

            }

            eventDB.close();

        } catch (SQLiteException se) {
            Toast.makeText(getContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("", se.getMessage());
        }


    }

    // GeneralDday의 값을 먼저 불러옴
    @Override
    public void onResume() {
        super.onResume();
        Open();

        //배치 어텝터 설정
        lv_event_list.setAdapter(new ScheduleAdmin.EventListAdapter());


    }

    // DB에 저장된 값을 불러온다
    protected void Open() {

        ShareData.mEventList.clear();
        try {
            String dbName = "eventDB";
            String tableName = "eventCal";

            SQLiteDatabase ReadDB = getContext().openOrCreateDatabase(dbName, MODE_PRIVATE, null);


            //SELECT문을 사용하여 테이블에 있는 데이터를 가져온다..
            Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName + " order by year asc,month asc,date asc", null);

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


                        // 리스트에 저장된 타이틀 갯수 확인
                        Log.d("MY", "--select title : " + title);


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
}