package com.teamproject.aaaaan_2.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hanman-yong on 2020-01-07.
 */
public class dbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aaaaan_2.db";
    private static final int DATABASE_VERSION = 17;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        // 회원 정보 테이블 생성
        db.execSQL("CREATE TABLE member ( " +
                "id TEXT PRIMARY KEY," +
                "password TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "tel TEXT NOT NULL, " +
                "address TEXT);"
        );

        // 이미지를 저장하기 위한 테이블 생성. 로그인한 유저의 id를 외래키로 사용하여 두 테이블을 연동한다.
        db.execSQL("CREATE TABLE member_image ( " +
                "idx INTEGER PRIMARY KEY AUTOINCREMENT," +
                "image BLOB, " +
                "img_id TEXT NOT NULL," +
                "CONSTRAINT img_id_fk FOREIGN KEY(img_id) REFERENCES member(id) ON DELETE CASCADE);"
        );

        String sql ="create table if not exists zzam_list(idx integer primary key autoincrement,name text,grade text,tel text, date text)";
        db.execSQL(sql);
    }

    // 버전을 변경하게되면 기존의 테이블을 지운 후에 다시 db를 생성한다.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if EXISTS member_image");
        db.execSQL("DROP TABLE if EXISTS member");
        db.execSQL("DROP TABLE if EXISTS zzam_list");

        onCreate(db);
    }
}
