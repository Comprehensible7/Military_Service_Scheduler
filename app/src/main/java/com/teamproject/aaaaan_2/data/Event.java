package com.teamproject.aaaaan_2.data;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Hugo Andrade on 25/03/2018.
 */

public class Event implements Serializable {

    private String mID;
    private String mTitle;
    private Calendar mDate;
    private int mColor;
    private boolean isCompleted;


    // 캘린더 내부 이벤트 생성 전 지정값을 선언
    public Event(String id, String title, Calendar date, int color, boolean isCompleted) {
        mID = id; // db저장 스트링 저장
        mTitle = title; // db저장 스트링 저장
        mDate = date; // db저장 <YMD 연,월,일 별도로> 모두 스트링으로 저장
        mColor = color; // db저장 스트링으로 저장
        this.isCompleted = isCompleted;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Calendar getmDate() {
        return mDate;
    }

    public void setmDate(Calendar mDate) {
        this.mDate = mDate;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public Calendar getDate() {
        return mDate;
    }

    public int getColor() {
        return mColor;
    }

    public String setID(String id) {
        return mID;
    }

    public String setTitle(String title) {
        return mTitle;
    }

    public Calendar setDate(Calendar c) {
        return mDate;
    }

    public int setColor(int color) {
        return mColor;
    }

    public boolean isCompleted() {
        return isCompleted;
    }



}
