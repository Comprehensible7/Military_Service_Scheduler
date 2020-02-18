package com.teamproject.aaaaan_2.member_adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.ui.menu.PersonListFragment;

public class MyAdapter extends BaseAdapter {

    // custom layout 항목 받아오기
    ImageView lv_img;
    TextView lv_name;
    TextView lv_content;

    //메인 엑티비티
   // MainActivity main;
    PersonListFragment pf;

    public MyAdapter(PersonListFragment pf) {
        this.pf = pf;
    }

    //리스트뷰에 보여질 Item 수 = 메인엑티비티의 아이템 배열 크기만큼
    @Override
    public int getCount() {
        return pf.mItems.size();
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public MyItem getItem(int position) {
        return pf.mItems.get(position);
    }

    //실제 보여지는 부분
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        //'listview_custom' Layout을 inflate하여 convertView 참조 획득
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom,parent,false);
        }

        //list_custom에 id적용
        lv_img = (ImageView)convertView.findViewById(R.id.lv_img);
        lv_name = (TextView)convertView.findViewById(R.id.lv_name);
        lv_content = (TextView)convertView.findViewById(R.id.lv_grade);

        MyItem myItem = pf.mItems.get(position);

        //셋팅된 아이템 뿌려주기
        lv_img.setImageResource(myItem.getImage_id());
        lv_name.setText(myItem.getName());
        lv_content.setText(myItem.getGrade());

        return convertView;
    }

    //세터 설정
    //아이콘
    public void setLv_img(Drawable img) {
        lv_img.setImageDrawable(img);
    }
    //이름
    public void setLv_name(String name) {
        lv_name.setText(name);
    }

    //계급부분
    public void setLv_content(String content) {
        lv_content.setText(content);
    }

}