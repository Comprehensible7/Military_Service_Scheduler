package com.teamproject.aaaaan_2.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.teamproject.aaaaan_2.MainActivity;
import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.database.dbHandler;
import com.teamproject.aaaaan_2.database.dbHelper;

/**
 * Created by hanman-yong on 2020-01-07.
 */
public class JoinActivity extends AppCompatActivity {

    dbHandler handler;

    EditText id, password, name, address, password2, tel;
    ImageView setImage, unsetImage;
    Button join, id_chack, cancel;
    static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        if (handler == null) {
            handler = dbHandler.open(this);
        }

        join = (Button) findViewById(R.id.last_join);
        id_chack = (Button) findViewById(R.id.join_id_chack);
        cancel = (Button) findViewById(R.id.last_cancel);


        id = (EditText) findViewById(R.id.join_id);
        password = (EditText) findViewById(R.id.join_password);
        password2 = (EditText) findViewById(R.id.join_password2);
        name = (EditText) findViewById(R.id.join_name);
        address = (EditText) findViewById(R.id.join_address);
        tel = (EditText) findViewById(R.id.join_tel);

        setImage = (ImageView) findViewById(R.id.Image_chack);
        unsetImage = (ImageView) findViewById(R.id.Image_chack);

        id_chack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(id);
                count++;

            }
        });

        //페스워드 일치 확인
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.getText().toString().equals(password2.getText().toString())) {
                    setImage.setImageResource(R.drawable.chack);
                } else
                    unsetImage.setImageResource(R.drawable.unchack);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert(join);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 취소 후 로그인 페이지로 이동
                finish();
            }
        });

    }

    public void search(EditText edit_id_check) {

        String id_chack = edit_id_check.getText().toString();
        Cursor cursor;
        boolean flag = true;

        cursor = handler.member_select(id_chack);

        while(cursor.moveToNext()) {
            if (cursor != null) {
                Toast.makeText(getApplicationContext(), "아이디 중복", Toast.LENGTH_SHORT).show();
                edit_id_check.setText("");
                flag = false;
            }
        }

        cursor.close();

        if (flag) {
            Toast.makeText(getApplicationContext(), "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void insert(View target) {
        String insert_id = id.getText().toString();
        String insert_password = password.getText().toString();
        String insert_password2 = password2.getText().toString();
        String insert_name = name.getText().toString();
        String insert_tel = tel.getText().toString();
        String insert_address = address.getText().toString();

        // 회원가입 양식에 빈칸이 있는 경우
        if (insert_id.length() == 0) {
            Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_password.length() == 0) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_password2.length() == 0) {
            Toast.makeText(getApplicationContext(), "비밀번호 확인란을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_name.length() == 0) {
            Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_tel.length() == 0) {
            Toast.makeText(getApplicationContext(), "전화번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 이메일 주소는 필수입력이 아니므로 입력이 안되어있으면 빈 문자열을 넣어준다.
        else if (insert_address.length() == 0) {
            insert_address = "";
        }

        if (this.count != 1) {
            Toast.makeText(getApplicationContext(), "아이디 중복 확인 해주세요.", Toast.LENGTH_SHORT).show();
            this.count = 0;

        } else if (insert_password.equals(insert_password2) == false && this.count >= 1) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();

            password.setText("");
            password2.setText("");

            this.count = 0;
            Toast.makeText(getApplicationContext(), count, Toast.LENGTH_SHORT).show();

        } else if (insert_password.equals(insert_password2) && this.count >= 1) {

            handler.member_insert(insert_id, insert_name, insert_password, insert_tel, insert_address);

            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();

            this.count = 0;

            // 회원가입이 성공하면 로그인 화면으로 이동
            finish();
        }
    }

}
