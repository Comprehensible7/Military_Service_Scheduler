package com.teamproject.aaaaan_2.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class DialogSampleUtil {

    public static void showConfirmDialog(Context context, String title, String message, final Handler handler){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(handler!=null)
                           handler.sendEmptyMessage(1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(handler!=null)
                            handler.sendEmptyMessage(0);
                        dialog.cancel();
                    }
                });
        AlertDialog dalog=builder.create();
        dalog.show();
    }

    public static void showMessageDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("닫기",null);
        AlertDialog dalog=builder.create();
        dalog.show();
    }

    //키패드 내리기
    public static void hideKeypad(Context context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
