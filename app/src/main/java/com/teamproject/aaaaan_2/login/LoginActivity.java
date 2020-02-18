package com.teamproject.aaaaan_2.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teamproject.aaaaan_2.MainActivity;
import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.database.dbHandler;
import com.teamproject.aaaaan_2.util.LoginSharedPreference;

/**
 * Created by hanman-yong on 2020-01-08.
 */
public class LoginActivity extends AppCompatActivity {
    public static final String AUTO_ID = "auto";             // 자동 로그인을 위한 상수
    public static final String SAVE_ID = "save";             // 아이디 저장을 위한 상수
    public static final String LOGIN_ID = "login";           // 로그인 정보를 저장하기 위한 상수. 이후 다른 페이지에서도 쓰임
    private static final String IS_CHECKED = "isChecked";    // 아이디 저장 체크박스의 체크여부를 저장하기 위한 상수

    // db 사용을 위한 핸들러
    dbHandler handler;

    ProgressDialog progressDialog;

    EditText id, password;

    Button btn_login, btn_join;

    CheckBox auto_login_check;
    CheckBox save_id_check;

    boolean auto_isCheck = false;
    boolean save_id_check_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // db 핸들러 오픈
        if (handler == null) {
            handler = dbHandler.open(this);
        }

        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);

        auto_login_check = (CheckBox) findViewById(R.id.check_auto_login);
        save_id_check = (CheckBox) findViewById(R.id.check_save_id);

        // 아이디 저장을 위해 체크박스의 체크여부를 받아와서 설정.
        save_id_check_ok = LoginSharedPreference.getChecked(LoginActivity.this, IS_CHECKED);
        save_id_check.setChecked(save_id_check_ok);

        // 아이디 저장 체크박스가 선택되어있으면 저장되어 있는 아이디 정보를 얻어온다.
        if(save_id_check_ok) {
            id.setText(LoginSharedPreference.getAttribute(LoginActivity.this, SAVE_ID));
        }

        //로그인 버튼 선언
        btn_login = (Button) findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("로그인중...");
                Intent in = new Intent(LoginActivity.this, MainActivity.class);
                if (search(id, password)) {
                    startActivity(in);
                    finish();
                }
            }
        });

        //회원 가입 버튼 선언
        btn_join = (Button) findViewById(R.id.login_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 바로 회원가입으로 이동
                Intent in = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(in);
            }
        });

        // 체크박스가 체크되어있을시에.
        // 자동 로그인
        auto_login_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_isCheck = ((CheckBox) v).isChecked();
                if(!auto_isCheck){
                    LoginSharedPreference.removeAttribute(LoginActivity.this, AUTO_ID);
                }
            }
        });

        save_id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_id_check_ok = ((CheckBox) v).isChecked();
                if(save_id_check_ok){
                    LoginSharedPreference.setChecked(LoginActivity.this, IS_CHECKED, save_id_check_ok);
                }

                else { // 체크가 해제되면 저장된 아이디를 해제하고 저장되어있던 체크박스의 상태여부도 초기화한다.
                    LoginSharedPreference.removeAttribute(LoginActivity.this, SAVE_ID);
                    LoginSharedPreference.removeChecked(LoginActivity.this, IS_CHECKED);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
    }

    // 로그인 버튼을 눌렀을 경우 아이디와 비밀번호를 조회해서 결과 전달
    public boolean search(EditText id, EditText pwd) {

        String id_check = id.getText().toString();
        String pw_check = pwd.getText().toString();

        boolean result = false;

        Cursor cursor_id_c;

        cursor_id_c = handler.member_select(id_check);

        String c_id = cursor_id_c.getString(cursor_id_c.getColumnIndex("id"));
        String c_pw = cursor_id_c.getString(cursor_id_c.getColumnIndex("password"));

        // 쿼리값이 제대로 나오는지 확인.
        Log.d("loginquery", c_id + " / " + c_pw);

        if (id_check.equals(c_id) && pw_check.equals(c_pw)) {
            result = true;
            Toast.makeText(getApplicationContext(), id_check + "님 환영합니다.", Toast.LENGTH_SHORT).show();

            // 아이디 저장이 체크? 그럼 아이디 저장해.
            if(save_id_check_ok) {
                LoginSharedPreference.setAttribute(LoginActivity.this, SAVE_ID ,id.getText().toString());
            }

            // 자동로그인 체크 되었을때만 정보를 저장하도록 수정해야함.
            if(auto_isCheck) {
                LoginSharedPreference.setAttribute(LoginActivity.this, AUTO_ID, id_check);
            }

            LoginSharedPreference.setAttribute(LoginActivity.this, LOGIN_ID, id_check);

            id.setText("");
            pwd.setText("");

        } else if (id_check.equals(c_id) && !pw_check.equals(c_pw)) {
            Toast.makeText(getApplicationContext(), "비밀번호 틀림", Toast.LENGTH_SHORT).show();
            id.setText("");
            pwd.setText("");
        } else {
            Toast.makeText(getApplicationContext(), "아이디 틀림", Toast.LENGTH_SHORT).show();
            id.setText("");
            pwd.setText("");
        }

        cursor_id_c.close();

        return result;
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

