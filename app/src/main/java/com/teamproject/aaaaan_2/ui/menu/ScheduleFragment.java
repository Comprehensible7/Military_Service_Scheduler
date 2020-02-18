package com.teamproject.aaaaan_2.ui.menu;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamproject.aaaaan_2.R;
import com.teamproject.aaaaan_2.uihelpers.TranslateAnimationBuilder;
import com.teamproject.calendarviewlib.CalendarView;

import java.util.Calendar;

public class ScheduleFragment extends Fragment {

    private View vCreateEventInnerContainer;
    private View vCreateEventOuterContainer;

    private String[] mShortMonths;
    private CalendarView mCalendarView;
    private OptionsAdapter mOptionsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_schedule_sdk21, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.schedule_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mCalendarView = rootView.findViewById(R.id.calendarView);
        mCalendarView.setOnMonthChangedListener(new CalendarView.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(int month, int year) {
                if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mShortMonths[month]);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(Integer.toString(year));
                }
            }
        });

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            int month = mCalendarView.getCurrentDate().get(Calendar.MONTH);
            int year = mCalendarView.getCurrentDate().get(Calendar.YEAR);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mShortMonths[month]);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(Integer.toString(year));
        }

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUnitSelector();
            }
        });

        vCreateEventInnerContainer = rootView.findViewById(R.id.create_event_inner_container);
        vCreateEventInnerContainer.setTranslationY(0);
        vCreateEventOuterContainer = rootView.findViewById(R.id.create_event_outer_container);
        vCreateEventOuterContainer.setVisibility(View.INVISIBLE);
        vCreateEventOuterContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUnitSelector();
            }
        });

        View tvCancel = rootView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUnitSelector();
            }
        });

        View tvSet = rootView.findViewById(R.id.tv_set);
        tvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUnitSelector();
                addEventToCalendarView(mOptionsAdapter.getSelectedColor());
            }
        });

        RecyclerView rvColors = rootView.findViewById(R.id.rv_colors);
        rvColors.setHasFixedSize(true);
        rvColors.setLayoutManager(new GridLayoutManager(getContext(), 6, LinearLayoutManager.VERTICAL, false));

        mOptionsAdapter = new OptionsAdapter(
                Color.rgb(159, 225, 231),
                Color.rgb(220, 39, 39),
                Color.rgb(219, 173, 255),
                Color.rgb(164, 189, 252),
                Color.rgb(84, 132, 237),
                Color.rgb(70, 214, 219),
                Color.rgb(122, 231, 191),
                Color.rgb(81, 183, 73),
                Color.rgb(251, 215, 91),
                Color.rgb(255, 184, 120),
                Color.rgb(255, 136, 124),
                Color.rgb(225, 225, 225)
        );
        mOptionsAdapter.setOnClickListener(new OptionsAdapter.OnClickListener() {
            @Override
            public void onClick(int color) {
                hideUnitSelector();
                addEventToCalendarView(color);
            }
        });
        rvColors.setAdapter(mOptionsAdapter);

        return rootView;
    }


    private void addEventToCalendarView(int color) {
        mCalendarView.addCalendarObject(new CalendarView.CalendarObject(
                null,
                null,
                mCalendarView.getSelectedDate(),
                color,
                Color.TRANSPARENT
        ));
    }

    private void showUnitSelector() {
        vCreateEventOuterContainer.setVisibility(View.VISIBLE);

        mOptionsAdapter.setSelectedItem(0);

        TranslateAnimationBuilder.instance()
                .setFromY(vCreateEventInnerContainer.getHeight())
                .setToY(0)
                .start(vCreateEventInnerContainer);
    }

    private void hideUnitSelector() {

        TranslateAnimationBuilder.instance()
                .setFromY(0)
                .setToY(vCreateEventInnerContainer.getHeight())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        vCreateEventOuterContainer.setVisibility(View.INVISIBLE);
                    }
                })
                .start(vCreateEventInnerContainer);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getLayoutInflater().inflate(R.menu.menu_toolbar_calendar_view, (ViewGroup) menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_today: {
                mCalendarView.setSelectedDate(Calendar.getInstance());
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    static class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

        final int[] mColors;
        int mSelectedItem;
        OnClickListener mListener;

        OptionsAdapter(int... colors) {
            mColors = colors;
            mSelectedItem = 0;
        }

        @Override
        public OptionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater vi = LayoutInflater.from(parent.getContext());
            return new ViewHolder(vi.inflate(R.layout.list_item_color, parent, false));
        }

        @Override
        public void onBindViewHolder(OptionsAdapter.ViewHolder holder, int position) {
            int color = mColors[holder.getAdapterPosition()];

            holder.cardViewInner.setCardBackgroundColor(color);

            if (holder.getAdapterPosition() == mSelectedItem) {
                holder.cardViewOuter.setCardBackgroundColor(Color.RED);
            }
            else {
                holder.cardViewOuter.setCardBackgroundColor(Color.TRANSPARENT);
            }
        }

        @Override
        public int getItemCount() {
            return mColors.length;
        }

        void setOnClickListener(OnClickListener listener) {
            mListener = listener;
        }

        void setSelectedItem(int position) {
            int oldPosition = mSelectedItem;
            mSelectedItem = position;

            notifyItemChanged(oldPosition);
            notifyItemChanged(mSelectedItem);
        }

        int getSelectedColor() {
            return mColors[mSelectedItem];
        }

        interface OnClickListener {
            void onClick(int color);
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            CardView cardViewInner;
            CardView cardViewOuter;

            ViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);

                cardViewInner = itemView.findViewById(R.id.cardView_inner);
                cardViewOuter = itemView.findViewById(R.id.cardView_outer);
            }

            @Override
            public void onClick(View v) {
                int oldPosition = mSelectedItem;
                mSelectedItem = getAdapterPosition();

                notifyItemChanged(oldPosition);
                notifyItemChanged(mSelectedItem);

                if (mListener != null)
                    mListener.onClick(mColors[mSelectedItem]);
            }
        }
    }
}
