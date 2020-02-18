package com.teamproject.aaaaan_2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hanman-yong on 2020-01-10.
 */
public class LoginSharedPreference {

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * 아이디 저장을 위해 만들었음.
     * @param ctx
     * @param key
     * @param value
     */
    public static void setChecked(Context ctx, String key, Boolean value){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // 값 읽기
    public static boolean getChecked(Context ctx, String key){
        return getSharedPreferences(ctx).getBoolean(key, false);
    }

    // 데이터 삭제
    public static void removeChecked(Context ctx, String key){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 자동로그인, 아이디저장, 현재 로그인정보를 위한 메소드들.
     * @param ctx
     * @param key
     * @param value
     */
    public static void setAttribute(Context ctx, String key, String value){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(key, value);
        editor.commit();
    }

    // 값 읽기
    public static String getAttribute(Context ctx, String key){
        return getSharedPreferences(ctx).getString(key, "");
    }

    // 데이터 삭제
    public static void removeAttribute(Context ctx, String key){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(key);
        editor.commit();
    }
}