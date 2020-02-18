package com.teamproject.aaaaan_2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teamproject.aaaaan_2.database.dbHandler;
import com.teamproject.aaaaan_2.login.JoinActivity;
import com.teamproject.aaaaan_2.login.LoginActivity;
import com.teamproject.aaaaan_2.util.LoginSharedPreference;

/**
 * Created by hanman-yong on 2020-01-02.
 */
public class IntroActivity extends Activity {
    private final String TAG = "D-Day Intro Act";

    ProgressDialog progressDialog;
    dbHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (handler == null) {
            handler = dbHandler.open(this);
        }

        // 2초간 인트로 화면을 띄운뒤에 메인 액티비티로 전환
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean check_db = check_DB();

                if (check_db == false) { // db에 정보가 없는 경우 바로 회원가입 메뉴로 이동
                    showProgress("회원가입으로 넘어갑니다.");
                    Intent intent = new Intent(IntroActivity.this, JoinActivity.class);
                    startActivity(intent);
                    finish();
                } else { // 있을경우 자동 로그인, 없다면 로그인화면
                    if (LoginSharedPreference.getAttribute(getApplication(), LoginActivity.AUTO_ID).length() == 0) {
                        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showProgress("로그인중...");
                        Toast.makeText(getApplicationContext(), LoginSharedPreference.getAttribute(getApplication(), LoginActivity.AUTO_ID) + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 2000);

    }

    // 현재 화면이 종료되면 프로그레스 다이얼로그도 지운다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
    }

    // 앱 최초 실행시 회원 테이블에 정보가 있는지 체크.
    public boolean check_DB() {
        // 쿼리를 실행한 후 결과값이 있을경우 true를 반환 없을경우 false를 반환한다.
        Cursor cursor;
        cursor = handler.member_select();
        Log.d(TAG, "" + cursor.getCount());

        while (cursor.moveToNext()) {
            if (cursor != null) {
                return true;
            }
        }

        return false;
    }

    public void showProgress(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
