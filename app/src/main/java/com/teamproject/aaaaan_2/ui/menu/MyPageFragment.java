package com.teamproject.aaaaan_2.ui.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.database.dbHandler;
import com.teamproject.aaaaan_2.login.LoginActivity;
import com.teamproject.aaaaan_2.util.LoginSharedPreference;

/**
 * Created by hanman-yong on 2019-12-30.
 */
public class MyPageFragment extends Fragment {
    private final String TAG = "D-Day MyPageFrag";

    String id_str, pw_str;

    dbHandler handler;

    EditText id, name, password, tel, email;
    EditText pw, pw_check;

    Button btn_update, btn_pw_change;

    LayoutInflater inflater;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;

        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_my_page, container, false);

        if (handler == null) {
            handler = dbHandler.open(getContext());
        }

        id = (EditText) rootView.findViewById(R.id.my_page_id);
        name = (EditText) rootView.findViewById(R.id.my_page_name);
        password = (EditText) rootView.findViewById(R.id.my_page_password);
        tel = (EditText) rootView.findViewById(R.id.my_page_tel);
        email = (EditText) rootView.findViewById(R.id.my_page_e_mail);

        btn_update = (Button) rootView.findViewById(R.id.my_page_update);
        btn_pw_change = (Button) rootView.findViewById(R.id.my_page_pw_change);

        MyInfo();

        // 개인정보 업데이트 버튼
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                onUpdate();

                password.setText("");
            }
        });

        // 비밀번호 변경 버튼
        btn_pw_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                onCreateDialog();
            }
        });

        return rootView;
    }

    // 처음 로그인한 유저의 정보를 뿌려주는 메소드
    public void MyInfo() {
        String my_id = LoginSharedPreference.getAttribute(getContext(), LoginActivity.LOGIN_ID);
        Log.d(TAG, my_id);

        Cursor cursor;
        cursor = handler.member_select(my_id);
        //cursor = sql_db.rawQuery("", null);
        Log.d(TAG, ""+cursor.getCount());
        cursor.moveToFirst();

        id.setText(cursor.getString(cursor.getColumnIndex("id")));
        name.setText(cursor.getString(cursor.getColumnIndex("name")));

        tel.setText(cursor.getString(cursor.getColumnIndex("tel")));
        email.setText(cursor.getString(cursor.getColumnIndex("address")));
    }

    // 정보 업데이트 할때 호출
    private void onUpdate() {
        id_str = id.getText().toString();
        pw_str = password.getText().toString();

        String name_str = name.getText().toString();
        String tel_str = tel.getText().toString();
        String email_str = email.getText().toString();

        Cursor cursor;

        cursor = handler.member_select(id_str);

        String ck_pw = cursor.getString(cursor.getColumnIndex("password"));

        if (pw_str.equals(ck_pw) == false) {
            Toast.makeText(getActivity(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            password.setText("");
            return;
        }

        handler.member_update(id_str, name_str, tel_str, email_str);

        Toast.makeText(getActivity(), "회원정보 수정 완료.", Toast.LENGTH_SHORT).show();

        password.setText("");

        onLog();

        cursor.close();

    }

    // 비밀번호 변경을 위한 다이얼로그 생성
    public void onCreateDialog() {
        id_str = id.getText().toString();
        pw_str = password.getText().toString();

        final Cursor cursor;

        cursor = handler.member_select(id_str);

        String ck_pw = cursor.getString(cursor.getColumnIndex("password"));

        if (pw_str.equals(ck_pw) == false) {
            Toast.makeText(getActivity(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            password.setText("");
            return;
        }

        // 레이아웃 XML파일을 View객체로 만들기 위해 inflater 사용
        View view = inflater.inflate(R.layout.fragment_pwd_change, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        pw = (EditText) view.findViewById(R.id.my_page_pw);
        pw_check = (EditText) view.findViewById(R.id.my_page_pw_check);

        builder.setTitle("변경할 비밀번호를 입력해 주세요.")
                .setView(view)
                .setPositiveButton("변경하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str_pw = pw.getText().toString();
                        String str_pw_check = pw_check.getText().toString();

                        if (str_pw.equals(str_pw_check)) {
                            handler.member_pw_update(id_str, str_pw);

                            Toast.makeText(getActivity(), "비밀번호 수정 완료.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "비밀번호가 일치하지 않습니다. 다시 시도해주세요. ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            onCreateDialog();
                        }

                        onLog();

                        password.setText("");

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

        cursor.close();
    }

    //비밀번호 변경이 잘 이루어졌는지 확인하기 위한 메소드 선언
    public void onLog(){
        id_str = id.getText().toString();

        Cursor cursor_c;
        cursor_c = handler.member_select(id_str);

        String c_id = cursor_c.getString(cursor_c.getColumnIndex("id"));
        String c_pw = cursor_c.getString(cursor_c.getColumnIndex("password"));
        String c_na = cursor_c.getString(cursor_c.getColumnIndex("name"));
        String c_te = cursor_c.getString(cursor_c.getColumnIndex("tel"));
        String c_ad = cursor_c.getString(cursor_c.getColumnIndex("address"));

        Log.d("loginquery", c_id + " / " + c_pw + " / " + c_na + " / " + c_te + " / " + c_ad);

        cursor_c.close();
    }
}