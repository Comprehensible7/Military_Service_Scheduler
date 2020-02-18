package com.teamproject.aaaaan_2.ui.mini_game_tabfrag;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.teamproject.aaaaan_2.R;

import java.util.Random;


public class NumberBaseBallGame extends Fragment {
    View view;

    //drawable에 있는 ic_bright.... 얘네들은 각각 click = 클릭했을때, default = 평소 상태, enable = 클릭을 할수 없을때의 버튼 색입니다
    int hitCount = 1;

    int[] comNumber = new int[4];
    TextView[] inputTextView = new TextView[4];
    Button[] numButtons = new Button[10];

    Button backSpace_btn;
    Button btn_ent;
    int count = 0;

    TextView result;
    ScrollView scroll;

    public NumberBaseBallGame() {
        // Required empty public constructor
    }

    public static NumberBaseBallGame newInstance() {
        NumberBaseBallGame NumberBaseBallGame = new NumberBaseBallGame();
        return NumberBaseBallGame;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_number_baseball, container, false);

        comNumber = getComNumber();
        for (int i = 0; i < inputTextView.length; i++) {
            //야구공에 들어갈 숫자(id)를 배열로 해서 받아온것
            inputTextView[i] = view.findViewById(R.id.input_1 + i);
        }
        for (int i = 0; i < numButtons.length; i++) {
            //숫자버튼(id)을 배열로 해서 받아온것
            numButtons[i] = view.findViewById(R.id.btn_0 + i);
        }
        //지우기버튼
        backSpace_btn = view.findViewById(R.id.btn_backspace);
        //전송버튼
        btn_ent = view.findViewById(R.id.btn_enter);

        result = view.findViewById(R.id.result);
        scroll = view.findViewById(R.id.scroll);

        for (Button getNumButton : numButtons) {
            getNumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TextView(야구공)에 숫자를 받아오기 위함
                    if (count < 4) {
                        Button button = view.findViewById(v.getId());
                        inputTextView[count].setText(button.getText().toString());
                        button.setEnabled(false);
                        count++;
                    } else {
                        //숫자 4개이상을 입력을 하려고할때 토스트메세지로 알려주는것
                        Toast.makeText(getContext(), "E버튼을 눌러주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //backspace버튼 리스너
        backSpace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //배열의 index가 0보다 작게될경우 에러나서 앱이 종료가 되기떄문에 if문으로 조건을 건다
                if (count > 0) {
                    //비활성화된 버튼을 다시 활성화 시키기 위해
                    // inputTextView는 스트링형태이기 떄문에 int형으로 형변환 해준다
                    int buttonEnableCount = Integer.parseInt(inputTextView[count - 1].getText().toString());
                    //버튼 비활성화 시킨버튼을 다시 활성화 시켜줌
                    numButtons[buttonEnableCount].setEnabled(true);
                    inputTextView[count - 1].setText("");
                    count--;
                } else {
                    //첫번째 숫자를 입력해야할 공간에서도 지우기 버튼을 눌렀을경우 토스트 메세지로 알린다.
                    Toast.makeText(getContext(), "숫자를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //엔터버튼 온클릭 리스너
        btn_ent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //숫자가 덜 입력되있을경우
                if (count < 4) {
                    Toast.makeText(getContext(), "숫자를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    int[] userNumber = new int[4];
                    for (int i = 0; i < userNumber.length; i++) {
                        userNumber[i] = Integer.parseInt(inputTextView[i].getText().toString());
                    }
                    //check[0] = 스트라이크, check[1] 볼
                    int[] check = new int[2];
                    check = getcheck(comNumber, userNumber);
                    /*Log.e("btn_ent", "check = S :" + check[0] + " B : " + check[1]);*/
                    String resultCount;
                    if (check[0] == 4) {
                        resultCount = hitCount + " [" + userNumber[0] + " " + userNumber[1] + " " + userNumber[2] + " " + userNumber[3] + "] 정답을 맞추셨습니다 축하드립니다!";
                    } else {
                        resultCount = hitCount + " [" + userNumber[0] + " " + userNumber[1] + " " + userNumber[2] + " " + userNumber[3] + "]" +
                                " S : " + check[0] + " B : " + check[1];
                    }
                    if (hitCount == 1) {
                        result.setText(resultCount + "\n");
                    } else {
                        result.append(resultCount + "\n");
                    }
                    //정답을 맞췄을경우 게임을 다시 시작
                    if (check[0] == 4) {
                        hitCount = 1;
                        comNumber = getComNumber();
                    } else {
                        //정답을 못맞췄을경우
                        hitCount++;
                    }


                    scroll.fullScroll(View.FOCUS_DOWN);

                    count = 0;

                    for (TextView textView : inputTextView) {
                        textView.setText("");
                    }

                    for (Button button : numButtons) {
                        button.setEnabled(true);
                    }

                }

            }
        });

        return view;
    }


    private int[] getcheck(int[] comNumber, int[] userNumber) {
        int[] setNum = new int[3];
        //스트라이크와 볼을 표시해주는 반복문
        for (int i = 0; i < comNumber.length; i++) {
            for (int j = 0; j < userNumber.length; j++) {
                if (comNumber[i] == userNumber[j]) {
                    if (i == j) {//스트라이크 일 경우
                        setNum[0]++;
                    } else {// 볼일경우
                        setNum[1]++;
                    }
                }
            }
        }
        return setNum;
    }

    public int[] getComNumber() {
        int[] setComNumber = new int[4];

        for (int i = 0; i < setComNumber.length; i++) {
            //컴퓨터의 숫자를 랜덤으로 생성
            setComNumber[i] = new Random().nextInt(10);
            for (int j = 0; j < i; j++) {
                if (setComNumber[i] == setComNumber[j]) {
                    i--;
                    break;
                }
            }
        }
        Log.e("setComNumber","setComNumber = " + setComNumber[0] +"," + setComNumber[1] +"," + setComNumber[2] +"," + setComNumber[3]);
        return setComNumber;
    }
}