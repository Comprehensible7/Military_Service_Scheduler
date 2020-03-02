package com.teamproject.aaaaan_2.ui.menu;

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
import com.teamproject.aaaaan_2.ui.mini_game_tabfrag.DrawinglotsGame;
import com.teamproject.aaaaan_2.ui.mini_game_tabfrag.NumberBaseBallGame;

/**
 * Created by hanman-yong on 2019-12-30.
 */
public class MiniGameFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MiniGameFragment() {
        // Required empty public constructor
    }

    public static MiniGameFragment newInstance(String param1, String param2) {
        MiniGameFragment fragment = new MiniGameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Fragment drawing_lotsGame;
    private Fragment number_baseballGame;

    private TabLayout minigame_tab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mini_game, container, false);



        drawing_lotsGame = new DrawinglotsGame();
        number_baseballGame = new NumberBaseBallGame();

        minigame_tab = (TabLayout) rootView.findViewById(R.id.mini_game_tab);
        minigame_tab.addTab(minigame_tab.newTab().setText("제비뽑기"));
        minigame_tab.addTab(minigame_tab.newTab().setText("숫자야구"));
//        tabLayout.setupWithViewPager(pager);

        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.mini_game_view);
        final MiniGameFragmentAdapter adapter = new MiniGameFragmentAdapter(getChildFragmentManager(), minigame_tab.getTabCount());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(minigame_tab));
        minigame_tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private class MiniGameFragmentAdapter extends FragmentPagerAdapter {
        int num_tab;

        public MiniGameFragmentAdapter(FragmentManager fm, int numOfTabs) {
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


                if( position == 0 ){
                return drawing_lotsGame;
            }else{
                return number_baseballGame;
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