package com.teamproject.aaaaan_2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.teamproject.aaaaan_2.member_adapter.MyItem;

/**
 * Created by hanman-yong on 2020-01-28.
 */
public class dbHandler {
    private final String TAG = "dbHandler";

    SQLiteOpenHelper mHelper = null;
    SQLiteDatabase mDB = null;

    public dbHandler(Context context) {
        mHelper = new dbHelper(context);
    }

    public static dbHandler open(Context context) {
        return new dbHandler(context);
    }

    // 회원 DB 관리.
    public Cursor member_select(String login_id) {
        mDB = mHelper.getReadableDatabase();

        String sql_query = "SELECT * FROM member WHERE id ='" + login_id + "'";
        Cursor c = mDB.rawQuery(sql_query, null);

        c.moveToFirst();

        return c;
    }

    public Cursor member_select() {
        mDB = mHelper.getReadableDatabase();

        String sql_query = "SELECT * FROM member";
        Cursor c = mDB.rawQuery(sql_query, null);
        //c.moveToFirst();

        return c;
    }

    public void member_insert(String id, String name, String password, String tel, String address) {

        Log.d(TAG, "member_insert");

        mDB = mHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put("id", id);
        value.put("name", name);
        value.put("password", password);
        value.put("tel", tel);
        value.put("address", address);

        mDB.insert("member", null, value);
    }

    public void member_update(String id, String name, String tel, String address) {
        Log.d(TAG, "member_update");

        mDB = mHelper.getWritableDatabase();

        String id_array[] = {id};

        ContentValues value = new ContentValues();
        value.put("name", name);
        value.put("tel", tel);
        value.put("address", address);

        mDB.update("member", value, "id = ?", id_array);
    }

    public void member_pw_update(String id, String password) {
        Log.d(TAG, "member_update");

        mDB = mHelper.getWritableDatabase();

        String id_array[] = {id};

        ContentValues value = new ContentValues();
        value.put("password", password);

        mDB.update("member", value, "id = ?", id_array);
    }

    public void member_delete(String id) {
        Log.d(TAG, "member_delete");

        mDB = mHelper.getWritableDatabase();

        mDB.delete("member", "id=?", new String[]{id});
    }


    // 이미지 DB 관리
    // sqlite blob 사이즈 1mb가 넘을 때 가져오는 방법
    // 오류 원인은 BLOB에 저장된 byte 용량이 너무 컸기 때문이다.
    // query 결과를 가져오는 CursorWindow 는 1MB 의 용량 제한이 있다.
    // 그래서 아래와같이 디비에 저장되어있는 blob 데이터를 짤라서 가져오는 방법을 사용한다.
    public byte[] image_parsing_select(String my_id) {
        mDB = mHelper.getReadableDatabase();
        byte[] img_bit = {};

        String img_query = "select length(image) from member_image where img_id ='" + my_id + "';";
        Cursor sizeCursor = mDB.rawQuery(img_query, null);

        if (sizeCursor.moveToNext()) {
            long blobStart = 1;
            long blobLen = 1;
            long blobSize = sizeCursor.getLong(0);

            byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null;

            while (blobSize > 0) {
                blobLen = blobSize > 1000000 ? 1000000 : blobSize;
                blobSize -= blobLen;
                String img_sub_query = "select substr(image ," + blobStart + ", " + blobLen + ") from member_image where img_id = '" + my_id + "';";
                Cursor blobCursor = mDB.rawQuery(img_sub_query, null);

                if (blobCursor.moveToNext()) {
                    byte[] barr = blobCursor.getBlob(0);
                    if (barr != null)
                        System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                }
                blobCursor.close();

                blobStart += blobLen;
            }

            if (bytes != null) {
                img_bit = bytes;
            }
        }
        sizeCursor.close();

        return img_bit;
    }

    public void member_image_insert(byte[] image, String img_id) {

        Log.d(TAG, "member_image_insert");

        mDB = mHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put("image", image);
        value.put("img_id", img_id);

        mDB.insert("member_image", null, value);
    }

    public void member_image_delete(String img_id) {
        Log.d(TAG, "member_image_delete");

        mDB = mHelper.getWritableDatabase();

        mDB.delete("member_image", "img_id=?", new String[]{img_id});
    }

    //전체 조회부분
    public Cursor select(){
        //helper 내용을 읽어올 수 있도록
        mDB = mHelper.getReadableDatabase();
        Cursor c = mDB.query("zzam_list",null,null,null,null,null,null);

        return c;
    }

    //DB 추가부분
    public void insert(MyItem myItem){
        Log.d(TAG, "INSERT부분");

        //helper내용에 덮어 쓸 수있도록
        mDB = mHelper.getWritableDatabase();

        //ContentValues 를 이용해서 내용을 추가하려했으나 제대로 들어가지 않았음
      /*  ContentValues value = new ContentValues();
        value.put("id",myItem.getId());
        value.put("name",myItem.getName());
        value.put("grade",myItem.getGrade());
        value.put("tel",myItem.getTel());
        value.put("date",myItem.getDate());*/
        //sql_db.insert("zzam_list",null,value);

        //SQL삽입 구문
        String sql = String.format("insert into zzam_list(name,grade,tel,date) values('%s','%s','%s','%s')",myItem.getName(),myItem.getGrade(),myItem.getTel(),myItem.getDate());

        mDB.execSQL(sql);
    }

    //삭제
    public void delete(int idx)
    {
        Log.d(TAG, "delete");
        mDB = mHelper.getWritableDatabase();

        String sql = String.format("delete from zzam_list where idx=%d",idx);
        mDB.execSQL(sql);
    }

    //수정
    public void update(MyItem myItem) {
        Log.d(TAG, "update");

        mDB = mHelper.getWritableDatabase();

        String sql = String.format("update zzam_list set name='%s', grade='%s', tel='%s',date='%s' where idx=%d",myItem.getName(),myItem.getGrade(),myItem.getTel(),myItem.getDate(),myItem.getIdx());

        Log.d(TAG,sql);
        Log.d(TAG, "grade : "+ myItem.getGrade());

        mDB.execSQL(sql);
    }

    public void close() {
        mHelper.close();
    }
}