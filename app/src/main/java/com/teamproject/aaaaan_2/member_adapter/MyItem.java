package com.teamproject.aaaaan_2.member_adapter;

import java.io.Serializable;

//직렬화함
public class MyItem implements Serializable {


    int idx; //DB Primary 값
    int image_id; //이미지 ID 저장할 변수
    String name;
    String grade;
    String tel;
    String date;

    public MyItem() {
    }

    public MyItem(String name, String grade, String tel, String date) {
        this.name = name;
        this.grade = grade;
        this.tel = tel;
        this.date = date;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }


    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
