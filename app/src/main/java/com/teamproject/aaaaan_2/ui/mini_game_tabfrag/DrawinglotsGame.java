package com.teamproject.aaaaan_2.ui.mini_game_tabfrag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.teamproject.aaaaan_2.R;

import java.util.Random;


public class DrawinglotsGame extends Fragment {
    View view;

    ImageView image_paper;
    Button btn_open;
    Button btn_new_game;
    Button btn_next;
    TextView info_text;

    int[] roll = new int[6];
    int n;

    int index = 0;

    public DrawinglotsGame() {
        // Required empty public constructor
    }

    public static DrawinglotsGame newInstance() {
        DrawinglotsGame tab1 = new DrawinglotsGame();
        return tab1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_drawing_lots, container, false);


        // 쪽지 이미지
        image_paper = view.findViewById(R.id.image_paper);
        // 다음 버튼
        btn_next = view.findViewById(R.id.btn_next);
        // 첫 안내 텍스트.
        info_text = view.findViewById(R.id.text_result);
        // 새게임
        btn_new_game = view.findViewById(R.id.btn_new_game);
        // 펼쳐보기
        btn_open = view.findViewById(R.id.btn_open);

        //새게임 버튼을 눌렀을때
        btn_new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //배열의 i번째 방에 0이면 생존 1이면 걸리는 게임
                //배열의 i번째 방을 0으로 초기화 시켜준다. (새게임을 위해서)
                for (int i = 0; i < roll.length; i++) {
                    roll[i] = 0;
                }

                info_text.setVisibility(View.GONE);

                //이미지를 초기화 시킨다. (새게임을 위해서)
                image_paper.setImageResource(0);

                //배열에 랜덤한 방에 1을 넣기위해서 배열의 크기는 6 {0, 1, 2, 3, 4, 5}
                n = new Random().nextInt(6);

                //배열의 랜덤한 방에 1을 넣는다
                roll[n] = 1;
                btn_new_game.setVisibility(View.GONE);
                btn_next.setVisibility(View.VISIBLE);
                btn_open.setVisibility(View.VISIBLE);
            }
        });

        // 다음 버튼 눌렀을경우 이미지 밑의 텍스트에 아래의 문장을 배치시킨다
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_paper.setImageResource(0);
            }
        });

        // 펼쳐보기 버튼을 눌렀을경우
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //배열의 방에 1이 들어있을경우 실행할 조건문(내기에 졌을경우)
                if (roll[index] == 1) {
                    //drawble파일에 지정해놓은 이미지를 불러온다.
                    image_paper.setImageResource(R.drawable.congratulation);
                    btn_next.setVisibility(View.GONE);
                    btn_open.setVisibility(View.GONE);
                    btn_new_game.setVisibility(View.VISIBLE);
                } else {
                    //배열의 방에 1이아닌게 들어가있을경우 (내기에서 이긴경우)
                    image_paper.setImageResource(R.drawable.paper);
                }
                //다음 배열의 방을 확인하기위해 버튼을 누를떄마다 index를 1씩 증가시킨다.
                index++;

                //index가 roll배열의 방의 길이보다 클경우 에러가 발생하므로 index를 0으로 초기화시킨다.
                if (index >= roll.length) index = 0;
            }
        });
        return view;
    }

}