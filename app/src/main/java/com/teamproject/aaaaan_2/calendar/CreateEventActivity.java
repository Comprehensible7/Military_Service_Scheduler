package com.teamproject.aaaaan_2.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.data.Event;
import com.teamproject.aaaaan_2.util.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CreateEventActivity extends AppCompatActivity {

    public static final int ACTION_DELETE = 1;
    public static final int ACTION_EDIT = 2;
    public static final int ACTION_CREATE = 3;

    private static final String INTENT_EXTRA_ACTION = "intent_extra_action";
    private static final String INTENT_EXTRA_EVENT = "intent_extra_event";
    private static final String INTENT_EXTRA_CALENDAR = "intent_extra_calendar";

    private static final int SET_DATE_AND_TIME_REQUEST_CODE = 200;

    private final static SimpleDateFormat dateFormat
            = new SimpleDateFormat("EEEE, dd/MM    HH:mm", Locale.getDefault());

    private Event mOriginalEvent; // 달력의 오리지널 이벤트

    private Calendar mCalendar; // Calendar
    private String mTitle; // mTitle = Title
    private boolean mIsComplete;
    private int mColor; // 글자를 입력할때도 Color를 사용한다.

    private boolean isViewMode = true;

    private EditText mTitleView; // 일정 입력 시 타이틀 입력부분이다
    private Switch mIsCompleteCheckBox; // 일정 입력부분 중 스위치 버튼
    private TextView mDateTextView; // 일정 입력부분 중 하단부의 시간 선택 가능
    private CardView mColorCardView; // 일정 입력 후 타이틀 옆부분의 색상을 선택가능
    private View mHeader; // 일정 입력부분의 취소와 저장가능한 부분

//    //SharedPreferences
//    public static String sfName = "myFile";


    // 인텐트 생성
    public static Intent makeIntent(Context context, @NonNull Calendar calendar) {
        return new Intent(context, CreateEventActivity.class).putExtra(INTENT_EXTRA_CALENDAR, calendar);
    }

    public static Intent makeIntent(Context context, @NonNull Event event) {
        return new Intent(context, CreateEventActivity.class).putExtra(INTENT_EXTRA_EVENT, event);
    }

    public static Event extractEventFromIntent(Intent intent) {
        return intent.getParcelableExtra(INTENT_EXTRA_EVENT);
    }

    public static int extractActionFromIntent(Intent intent) {
        return intent.getIntExtra(INTENT_EXTRA_ACTION, 0);
    }

    public static Calendar extractCalendarFromIntent(Intent intent) {
        return (Calendar) intent.getSerializableExtra(INTENT_EXTRA_CALENDAR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        // 지난번 저장해놨던 사용자 입력값을 꺼내서 보여주기
//        SharedPreferences sf = getSharedPreferences(sfName, 0);
//        String str = sf.getString("name", ""); // 키값으로 꺼냄
//        mTitleView.setText(str);



        setResult(RESULT_CANCELED);

        extractDataFromIntentAndInitialize();

        initializeUI();
    }

    // 옵션메뉴를 생성했을경우
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);

        return true;
    }

    // 옵션 셀렉트 되었을경우
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                delete();
                return true;
            }
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // 입력되어있는 Calendar의 데이터를 추출한다
    private void extractDataFromIntentAndInitialize() {

        mOriginalEvent = extractEventFromIntent(getIntent());

        if (mOriginalEvent == null) {
            mCalendar = extractCalendarFromIntent(getIntent());
            if (mCalendar == null)
                mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.HOUR_OF_DAY, 8);
            mCalendar.set(Calendar.MINUTE, 0);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);
            mColor = ColorUtils.mColors[0];
            mTitle = "";
            mIsComplete = false;
            isViewMode = false;
        } else {
            mCalendar = mOriginalEvent.getDate();
            mColor = mOriginalEvent.getColor();
            mTitle = mOriginalEvent.getTitle();
            mIsComplete = mOriginalEvent.isCompleted();
            isViewMode = true;
        }
    }

    // UI구성 및 작동을 위한 구성요소
    private void initializeUI() {
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = findViewById(R.id.schedule_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mHeader = findViewById(R.id.ll_header);
        mHeader.setVisibility(View.VISIBLE);

        setupToolbar();

        View tvSave = mHeader.findViewById(R.id.tv_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        View tvCancel = mHeader.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

                if (mOriginalEvent == null)
                    overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
            }
        });

        mDateTextView = findViewById(R.id.tv_date);
        mDateTextView.setText(dateFormat.format(mCalendar.getTime()));
        mDateTextView.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                Activity context = CreateEventActivity.this;
                Intent intent = SelectDateAndTimeActivity.makeIntent(context, mCalendar);

                startActivityForResult(intent,
                        SET_DATE_AND_TIME_REQUEST_CODE,
                        ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
            }
        });

        mColorCardView = findViewById(R.id.cardView_event_color);
        mColorCardView.setCardBackgroundColor(mColor);
        mColorCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectColorDialog.Builder.instance(CreateEventActivity.this)
                        .setSelectedColor(mColor)
                        .setOnColorSelectedListener(new SelectColorDialog.OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int color) {
                                mColor = color;
                                mColorCardView.setCardBackgroundColor(mColor);
                            }
                        })
                        .create()
                        .show();
            }
        });
        mTitleView = findViewById(R.id.et_event_title);
        mTitleView.setText(mTitle);
        mIsCompleteCheckBox = findViewById(R.id.checkbox_completed);
        mIsCompleteCheckBox.setChecked(mIsComplete);

        if (isViewMode) {
            mIsCompleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setupEditMode();
                    mIsCompleteCheckBox.setOnCheckedChangeListener(null);
                }
            });
            mTitleView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    setupEditMode();
                    mTitleView.setOnFocusChangeListener(null);
                }
            });
        } else {
            setupEditMode();
        }
    }

    // 수정할수 있게 해준다
    private void setupEditMode() {
        if (isViewMode) {
            isViewMode = false;
            setupToolbar();
        }
    }

    // 툴바 셋업
    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            if (isViewMode)
                getSupportActionBar().show();
            else
                getSupportActionBar().hide();
        }

        if (mHeader != null) {
            mHeader.setVisibility(isViewMode ? View.GONE : View.VISIBLE);
        }
    }

    // 삭제
    private void delete() {
        Log.e(getClass().getSimpleName(), "delete");

        setResult(RESULT_OK, new Intent()
                .putExtra(INTENT_EXTRA_ACTION, ACTION_DELETE)
                .putExtra(INTENT_EXTRA_EVENT, mOriginalEvent));
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);

    }

    // 이벤트 저장
    private void save() {

        int action = mOriginalEvent != null ? ACTION_EDIT : ACTION_CREATE;
        String id = mOriginalEvent != null ? mOriginalEvent.getID() : generateID();
        String rawTitle = mTitleView.getText().toString().trim();

        mOriginalEvent = new Event(
                id,
                rawTitle.isEmpty() ? null : rawTitle,
                mCalendar,
                mColor,
                mIsCompleteCheckBox.isChecked()
        );


        setResult(RESULT_OK, new Intent()
                .putExtra(INTENT_EXTRA_ACTION, action)
                .putExtra(INTENT_EXTRA_EVENT, mOriginalEvent));
        finish();

        if (action == ACTION_CREATE)
            overridePendingTransition(R.anim.stay, R.anim.slide_out_down);

    }



//    @Override
//    protected void onStop() {
//        super.onStop();
//        // Activity 가 종료되기 전에 저장한다
//        // SharedPreferences 에 설정값(특별히 기억해야할 사용자 값)을 저장하기
//        SharedPreferences sf = getSharedPreferences(sfName, 0);
//        SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요
//        String str = mTitleView.getText().toString(); // 사용자가 입력한 값
//        editor.putString("name", str); // 입력
//        //editor.putString("xx", "xx"); // 입력
//        editor.commit(); // 파일에 최종 반영함
//    }




    // 액티비티 결과값
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_DATE_AND_TIME_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                mCalendar = SelectDateAndTimeActivity.extractCalendarFromIntent(data);
                mDateTextView.setText(dateFormat.format(mCalendar.getTime()));

                setupEditMode();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static String generateID() {
        return Long.toString(System.currentTimeMillis());
    }


}




