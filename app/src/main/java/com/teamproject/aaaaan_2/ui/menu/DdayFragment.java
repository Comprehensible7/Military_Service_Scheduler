package com.teamproject.aaaaan_2.ui.menu;

// GeneralDday 결과값 출력id : result
// Main_First 결과값 출력id : aDay
// 글자입력부분 : multipleTriangleView 283번째 라인부터
// 일정등록 프로세스관련 내용은 CalendarView, YMDCalendar 내용 분석 중
// to Calendar -> Calendar -> Event -> List -> 출력
// YMDCalendar 51번째줄부터 위의 내용나옴
// GeneralDday : 99번째 줄부터
// CalendarSDK21 : 167번째 줄부터
// CreateEventActivity : SharedPreferences -> 67번째 줄, 94 ~ 97번째 줄, 338 ~ 349번째 줄

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.ui.d_day_tabfrag.DdayAdmin;
import com.teamproject.aaaaan_2.ui.d_day_tabfrag.ScheduleAdmin;


public class DdayFragment extends Fragment {

    // 신경 ㄴㄴ
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // 신경 ㄴㄴ
    private String mParam1;
    private String mParam2;

    // 생성자
    public DdayFragment() {
        // Required empty public constructor
    }

    // 신경 ㄴㄴ
    public static DdayFragment newInstance(String param1, String param2) {
        DdayFragment fragment = new DdayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // 신경 ㄴㄴ
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // 탭 레이아웃을 나눠서 등록할 프래그먼트와 탭 레이아웃 선언
    private Fragment scheduleAdmin;
    private Fragment d_dayAdmin;

    private TabLayout d_day_tab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_d_day, container, false);

        // 프래그먼트 받아오기
        scheduleAdmin = new ScheduleAdmin();
        d_dayAdmin = new DdayAdmin();

        // 각 텝 제목 설정
        d_day_tab = (TabLayout) rootView.findViewById(R.id.d_day_tab);
        d_day_tab.addTab(d_day_tab.newTab().setText("일반 일정"));
        d_day_tab.addTab(d_day_tab.newTab().setText("전역일 등록"));

        // 뷰 페이저 선언 및 탭에 들어갈 어뎁터 선언
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.d_day_view);
        final DdayFragment.DdayFragmentAdapter adapter = new DdayFragment.DdayFragmentAdapter(getChildFragmentManager(), d_day_tab.getTabCount());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(d_day_tab));
        d_day_tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }

    // 프래그먼트를 페이지로 나눌 어뎁터 클래스 선언
    private class DdayFragmentAdapter extends FragmentPagerAdapter {
        int num_tab;

        public DdayFragmentAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.num_tab = numOfTabs;
        }

        /**
         * View Pager 의 Fragment 들은 각각 Index 를 가진다.
         * Android OS로 부터 요청된 Pager 의 Index 를 보내주면,
         * 해당되는 Fragment 를 리턴시킨다.
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {

            if ( position == 0 ){
                return scheduleAdmin;
            }
            else {
                return d_dayAdmin;
            }

        }

        /**
         * View Pager 에 몇개의 Fragment 가 들어가는지 설정한다.
         * @return
         */
        @Override
        public int getCount() {
            return num_tab;
        }
    }
}