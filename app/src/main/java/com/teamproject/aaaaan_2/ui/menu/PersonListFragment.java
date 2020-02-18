package com.teamproject.aaaaan_2.ui.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.database.dbHandler;
import com.teamproject.aaaaan_2.member_adapter.MyAdapter;
import com.teamproject.aaaaan_2.member_adapter.MyItem;

import java.util.ArrayList;

/**
 * Created by hanman-yong on 2019-12-30.
 */
public class PersonListFragment extends Fragment {

    //리스트뷰
    private ListView mListView;
    //인원 추가버튼
    private Button btn_add;

    //아이템을 세트로 담기 위한 배열
    public ArrayList<MyItem> mItems = new ArrayList<MyItem>();

    // 핸들러
    dbHandler handler;

    // 커서
    Cursor cursor=null;

    // 다이얼로그에서 벗어나서 인텐트 처리 요청하기 위한 handler선언
    Handler handler_s;

    // 메소드 안쪽에 있는 d_item 값을 넣기 위해 선언
    MyItem g_item;

    LayoutInflater inflater;

    // 수정폼에서 쓰일것들
    Spinner modify_spinner_grade;
    EditText modify_edit_name;
    EditText modify_edit_tel;
    EditText modify_edit_date;

    // 입력폼에서 쓰일것들
    Spinner insert_spinner_grade;
    EditText insert_name;
    EditText insert_tel;
    EditText insert_date;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_person_list, container, false);

        //메인 리스트뷰
        mListView = (ListView) rootView.findViewById(R.id.main_list);

        //리스트에 추가 버튼
        btn_add = (Button) rootView.findViewById(R.id.btn_member_add);

        //DB핸들러 불러오기(DB열기용)
        if (handler == null) {
            handler = dbHandler.open(getContext());
        }

        //리스트뷰를 클릭했을때 아이템 내용이 보이도록
        mListView.setOnItemClickListener(new click_listView());
        //리스트뷰를 길게 클릭했을때 아이템 삭제확인
        mListView.setOnItemLongClickListener(new longclick_listView());

        //더하기 버튼 눌렀을때
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력폼 불러오기
                onInsertDialog();
            }
        });
        //리스트뷰를 불러오는 메소드
        zzam_info();

        return rootView;
    }

    //출처: https://gandus.tistory.com/476 [Gandus Blog.]
    //리스트뷰의 항목을 짧게 터치 했을때
    private class click_listView implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //리스트 뷰에서 아이템을 터치했을때 수정버튼을 누르면 핸들러로 이동
//            handler_s = new Handler(){
//                @Override
//                public void handleMessage(@NonNull Message msg) {
//                    super.handleMessage(msg);
//                    if(msg.what==1){
//                        Log.d("ZZAM", "계급수정버튼 눌렀다");
//                        // 선택한 인원에 대한 정보를 전달하며 수정폼을 띄운다.
//
//                        onModifyDialog(g_item);
//                        Log.d("ZZAM", "계급수정버튼 눌렀다:넘어간다");
//                    }
//                }
//            };

            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

            //확인버튼을 터치하면 다이얼로그 사라짐
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            //수정버튼을 터치하면 핸들러를 통해 modify_form으로 인텐트
            alert.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   // handler_s.sendEmptyMessage(1);
                    onModifyDialog(g_item);
                    //dialog.dismiss();
                }
            });

            //리스트뷰에서 터치된 항목의 이름, 계급, 연락처, 입대년월 정보를 담음
            final MyItem d_item = (MyItem)mItems.get(position);

            //핸들러로 보내기 위해 전역변수에 담음
            g_item= d_item;

            int    idx = d_item.getIdx();
            String d_name = d_item.getName();
            String d_grade = d_item.getGrade();
            String d_tel = d_item.getTel();
            String d_date = d_item.getDate();

            Log.d("ZZAM", "IDX : "+ idx);
            Log.d("ZZAM", "이름 : "+ d_name);
            Log.d("ZZAM", "계급 : "+d_grade);
            Log.d("ZZAM", "번호 : "+d_tel);
            Log.d("ZZAM", "입대년월 : "+d_date);

            //리스트뷰에서 항목을 터치했을 때, 상세항목 출력
            String msg = "이름 : " + d_name + "\n" + "계급 : " + d_grade + "\n"+"전화번호 : " + d_tel + "\n" + "입대년월 : "+d_date;
            alert.setMessage(msg);
            alert.show();
        }
    }


    // Long click된 item의 index(position)을 기록한다.
    int selectedPos =-1;

    //리스트뷰를 길게 클릭했을 경우
    private class longclick_listView implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            selectedPos = position;
            AlertDialog.Builder d_alert = new AlertDialog.Builder(view.getContext());
            d_alert.setTitle("삭제");

            //예 클릭
            d_alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //포지션값 담기
                    MyItem item = mItems.get(position);

                    //배열에서 해당 포지션에 있는 값 지우기
                    mItems.remove(position);

                    //DB에서 idx값을 이용해 삭제
                    zzam_delete(item.getIdx());

                    dialog.dismiss();
                }
            });

            //아니오 클릭
            d_alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            d_alert.setMessage("삭제하시겠습니까?");
            d_alert.show();

            return true;
        }
    }

    //리스트항목을 MyAdapter를 통해서 리스트뷰에 넣어주기
    //항목의 계급에 따라서 이미지 구분
    public void dataSetting_list(int idx,String name, String grade, String tel, String date) {

        if(grade.equals("이병")){
            load_addItem(R.drawable.first,idx,name,grade,tel,date);
        }else if(grade.equals("일병")){
            load_addItem(R.drawable.second,idx,name,grade,tel,date);
        }else if(grade.equals("상병")){
            load_addItem(R.drawable.third,idx,name,grade,tel,date);
        }else if(grade.equals("병장")){
            load_addItem(R.drawable.end,idx,name,grade,tel,date);
       /* }else{
            load_addItem(R.drawable.ay01,idx,name,grade,tel,date);*/
        }

        //리스트뷰에 어댑터 적용
        mListView.setAdapter(new MyAdapter(this));
    }

    //DB에 저장된 내용을 불러와서 리스트 항목에 넣기
    public void load_addItem(int image_id,int idx,String name, String grade, String tel, String date){

        MyItem myItem = new MyItem();

        //MyItem에 내용 세팅
        myItem.setIdx(idx);
        myItem.setImage_id(image_id);
        myItem.setName(name);
        myItem.setGrade(grade);
        myItem.setTel(tel);
        myItem.setDate(date);

        mItems.add(myItem);
    }

    //DB테이블 짬리스트(zzam_list)에 저장된 짬정보 넣기
    public void zzam_info(){

        //이전데이터 지우기
        mItems.clear();
        try {
            //커서를 이용해서 조회
            cursor = handler.select();

            //커서를 컬럼값 단위로 넘김
            while(cursor.moveToNext()) {

                int idx = cursor.getInt(cursor.getColumnIndex("idx"));
                String str_name = cursor.getString(cursor.getColumnIndex("name"));
                String str_grade = cursor.getString(cursor.getColumnIndex("grade"));
                String str_tel = cursor.getString(cursor.getColumnIndex("tel"));
                String str_date = cursor.getString(cursor.getColumnIndex("date"));
                dataSetting_list(idx,str_name, str_grade,str_tel,str_date);

                Log.d("ZZAM","idx=" + idx + " grade=" + str_grade) ;

            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    //DB 데이터 추가
    public void zzam_insert(MyItem myItem){

        Log.d("ZZAM", "짬리스트 Insert");

        handler.insert(myItem);

        //갱신
        zzam_info();

        mListView.setAdapter(new MyAdapter(this));

        Log.d("ZZAM", "짬리스트 Insert 끝");

    }

    //DB 데이터 삭제
    public void zzam_delete(int idx){

        Log.d("ZZAM", "짬리스트 delete");

        handler.delete(idx);

        //갱신
        zzam_info();

        mListView.setAdapter(new MyAdapter(this));
    }

    //DB 데이터 수정
    public void zzam_update(MyItem myItem){
        handler.update(myItem);

        //갱신
        zzam_info();

        mListView.setAdapter(new MyAdapter(this));
    }

    // 입력폼 다이얼로그
    public void onInsertDialog() {
        // 레이아웃 XML파일을 View객체로 만들기 위해 inflater 사용
        View view = inflater.inflate(R.layout.person_list_insert_form, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        insert_name = (EditText) view.findViewById(R.id.list_modify_name);
        insert_spinner_grade = (Spinner) view.findViewById(R.id.insert_spinner_grade);
        insert_tel = (EditText) view.findViewById(R.id.list_modify_tel);
        insert_date = (EditText) view.findViewById(R.id.list_modify_date);

        //계급을 /res/values/arrays.xml에 있는 항목 중에 선택하기 위한 설정
        ArrayAdapter gradeAdapter =ArrayAdapter.createFromResource(getContext(), R.array.grade_list, android.R.layout.simple_spinner_item);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //안드로이드 스튜디오에 있는 기본값
        insert_spinner_grade.setAdapter(gradeAdapter); //위에서 설정한 어댑터를 스피너에 적용

        builder.setView(view)
                .setPositiveButton("입력하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("ZZAM", "입버튼 눌렀다");

                        //각 항목에 입력된 값을 문자열로 저장
                        String m_name = insert_name.getText().toString().trim();
                        String m_tel = insert_tel.getText().toString().trim();
                        String m_grade = insert_spinner_grade.getSelectedItem().toString().trim();
                        String m_date = insert_date.getText().toString().trim();

                        zzam_insert(new MyItem(m_name, m_grade, m_tel, m_date));

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

    // 수정폼 다이얼로그
    public void onModifyDialog(MyItem myItem) {
        // 레이아웃 XML파일을 View객체로 만들기 위해 inflater 사용
        View view = inflater.inflate(R.layout.person_list_modify_form, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        modify_edit_name = (EditText) view.findViewById(R.id.list_modify_name);
        modify_edit_tel = (EditText) view.findViewById(R.id.list_modify_tel);
        modify_edit_date = (EditText) view.findViewById(R.id.list_modify_date);

        //기존에 DB에 있는 정보를 수정폼에 미리 세팅
        String name = myItem.getName();
        String date = myItem.getDate();
        String tel = myItem.getTel();

        Log.d("ZZAM", "이름 : "+ name);
        Log.d("ZZAM", "입대일 : "+ date);
        Log.d("ZZAM", "번호 : "+ tel);

        modify_edit_name.setText("" + name);
        modify_edit_tel.setText("" + tel);
        modify_edit_date.setText("" + date);

        // /res/values/arrays.xml 의 값 중복 사용하지 않도록 계급값 넣는 배열 설정
        String[] grade_array = {"이병", "일병", "상병", "병장"};
        //스피너로 계급 선택할부분
        modify_spinner_grade = (Spinner) view.findViewById(R.id.modify_spinner_grade);
        ArrayAdapter gradeAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, grade_array);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modify_spinner_grade.setAdapter(gradeAdapter);

        builder.setView(view)
                .setPositiveButton("변경하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("ZZAM", "수정버튼 눌렀다");

                        //각 항목에 입력된 값을 문자열로 저장
                        String m_name = modify_edit_name.getText().toString().trim();
                        String m_tel = modify_edit_tel.getText().toString().trim();
                        String m_grade = modify_spinner_grade.getSelectedItem().toString().trim();

                        //직렬화로 보내기 위해 myItem에 내용 담기
                        myItem.setName(m_name);
                        myItem.setTel(m_tel);
                        myItem.setGrade(m_grade);

                        zzam_update(myItem);

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