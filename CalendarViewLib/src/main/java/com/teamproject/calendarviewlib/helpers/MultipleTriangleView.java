package com.teamproject.calendarviewlib.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import org.hugoandrade.calendarviewlib.R;

import java.util.ArrayList;
import java.util.List;

public class MultipleTriangleView extends View {

    private class TriangleAttr {

        private Paint mPaint; // 타이틀을 그릴 수 있게 해줌
        private Paint mBackgroundPaint; // 배경

        private Path mTrianglePath; //
        private Path mBackgroundPath; //

        private Direction mDirection; //

        private int mColor; // 색상
        private int mBackgroundColor; // 배경색상
        private String mTitle; // 타이틀

    }

    private static final Direction DEFAULT_DIRECTION = Direction.TOP_LEFT;
    private static final int DEFAULT_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

    // private로 선언된 class를 리스트화 한다
    private List<TriangleAttr> mTriangleAttr;
    //private List<String> title_list = new ArrayList<String>();

    private float mSeparatorWidth; // 폭
    private ViewDirection mViewDirection; // 보여지는 위치

    public MultipleTriangleView(Context context) {
        this(context, null);
    }

    public MultipleTriangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleTriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Direction mDirection;
        int mColor;
        int mBackgroundColor;
        int mNumberOfItems;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultipleTriangleView);
            switch (a.getInt(R.styleable.MultipleTriangleView_mtr_direction, 0)) {
                case 0:
                    mDirection = Direction.TOP_LEFT;
                    break;
                case 1:
                    mDirection = Direction.TOP_RIGHT;
                    break;
                case 2:
                    mDirection = Direction.BOTTOM_LEFT;
                    break;
                case 3:
                default:
                    mDirection = Direction.BOTTOM_RIGHT;
            }
            switch (a.getInt(R.styleable.MultipleTriangleView_mtr_view_direction, 0)) {
                case 0:
                    mViewDirection = ViewDirection.HORIZONTAL;
                    break;
                case 1:
                    mViewDirection = ViewDirection.VERTICAL;
                    break;
                default:
                    mViewDirection = ViewDirection.VERTICAL;
            }
            mColor = a.getColor(R.styleable.MultipleTriangleView_mtr_color, DEFAULT_COLOR);
            mBackgroundColor = a.getColor(R.styleable.MultipleTriangleView_mtr_background_color, DEFAULT_BACKGROUND_COLOR);

            mSeparatorWidth = a.getDimension(R.styleable.MultipleTriangleView_mtr_separator_width, 0);
            mNumberOfItems = a.getInteger(R.styleable.MultipleTriangleView_mtr_number_of_items, 1);
            a.recycle();
        } else {
            mDirection = DEFAULT_DIRECTION;
            mViewDirection = ViewDirection.VERTICAL;

            mColor = DEFAULT_COLOR;
            mBackgroundColor = DEFAULT_BACKGROUND_COLOR;

            mSeparatorWidth = 0;
            mNumberOfItems = 1;
        }

        mTriangleAttr = new ArrayList<>();
        for (int i = 0 ; i < mNumberOfItems ; i++) {
            TriangleAttr t = new TriangleAttr();

            t.mDirection = mDirection;
            t.mBackgroundColor = mBackgroundColor;
            t.mColor = mColor;

            t.mPaint = new Paint();
            t.mPaint.setStyle(Paint.Style.FILL);
            t.mPaint.setColor(t.mColor);
            t.mPaint.setAntiAlias(true);

            t.mBackgroundPaint = new Paint();
            t.mBackgroundPaint.setStyle(Paint.Style.FILL);
            t.mBackgroundPaint.setColor(t.mBackgroundColor);
            t.mBackgroundPaint.setAntiAlias(true);

            mTriangleAttr.add(t);
        }

        mViewDirection = ViewDirection.VERTICAL;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 0;
        int desiredHeight = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    public int getNumberOfItems() {
        return mTriangleAttr.size();
    }

    /**
     * Set the color of the triangle.
     * @param color the color of the triangle.
     */
    public void setColor(int color) {
        boolean somethingHasChanged = false;
        for (TriangleAttr t : mTriangleAttr) {
            somethingHasChanged = somethingHasChanged || setColor(t, color);
        }

        if (somethingHasChanged)
            invalidate();
    }

    // 일정 등록 시 저장되는 타이틀 선언
    public void setTitle(int i,String title){
        if (i >= mTriangleAttr.size())
            return ;
        mTriangleAttr.get(i).mTitle = title;

    }


    // 색상지정
    public boolean setColor(int i, int color) {
        if (i >= mTriangleAttr.size())
            return false;

        boolean somethingHasChanged = setColor(mTriangleAttr.get(i), color);

        if (somethingHasChanged)
            invalidate();

        return true;
    }
    // 색상지정
    private boolean setColor(TriangleAttr t, int color) {
        if (t.mColor != color) {
            t.mColor = color;
            if (t.mPaint != null) {
                t.mPaint.setColor(color);
            }
            t.mTrianglePath = null;
            return true;
        }
        return false;
    }
    // 배경
    public void setTriangleBackgroundColor(int color) {
        boolean somethingHasChanged = false;

        for (TriangleAttr t : mTriangleAttr) {
            somethingHasChanged = somethingHasChanged || setTriangleBackgroundColor(t, color);
        }

        if (somethingHasChanged)
            invalidate();
    }

    public boolean setTriangleBackgroundColor(int i, int color) {
        if (i >= mTriangleAttr.size())
            return false;

        boolean somethingHasChanged = setTriangleBackgroundColor(mTriangleAttr.get(i), color);

        if (somethingHasChanged)
            invalidate();

        return true;
    }
    // 배경
    private boolean setTriangleBackgroundColor(TriangleAttr t, int color) {
        if (t.mBackgroundColor != color) {
            t.mBackgroundColor = color;
            if (t.mBackgroundPaint != null) {
                t.mBackgroundPaint.setColor(color);
            }
            t.mBackgroundPath = null;
            return true;
        }
        return false;
    }

    /**
     * Set the direction of the triangle.
     * @param direction the direction of the triangle.
     */
    public void setDirection(Direction direction) {
        for (TriangleAttr t : mTriangleAttr) {

            if (direction != t.mDirection) {
                t.mDirection = direction;
                t.mTrianglePath = null;
            }
        }
        invalidate();
    }

    //
    // View Overrides
    //

    // paint형식으로 title값을 호출하여 달력에 표시하게끔 해준다.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTriangleAttr.size() < 1)
            return;

        float width = getWidth() - getPaddingEnd() - getPaddingStart();
        float height = getHeight() - getPaddingTop() - getPaddingBottom();

        float separatorWidthTotal = (mTriangleAttr.size() - 1) * mSeparatorWidth;


        if (mViewDirection == ViewDirection.VERTICAL) {
            if (separatorWidthTotal > width)
                return;

            float iheight = (height - separatorWidthTotal) / mTriangleAttr.size();

            float startX = getPaddingStart();
            float startY = getPaddingTop();


            // 캘린더 일정 등록 후 타이틀 출력 text형식이 아닌 Paint형식으로써 그려주는 부분
            for (TriangleAttr t : mTriangleAttr) {
                // X축 Y축 가로 세로 위치, 배경에 페인팅변수 선언
                canvas.drawPath(getBackgroundPath(t, startX, startY, width, iheight), t.mBackgroundPaint);
                canvas.drawPath(getTrianglePath(t, startX, startY, width, iheight), t.mPaint);

                // 타이틀이 null일때, 페인트 생성, 선택한 위치(날짜)에 스타일, 색상, 텍스트 사이즈를 지정
                if(t.mTitle!=null) {
                    Paint tPaint = new Paint();
                    tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    tPaint.setColor(Color.WHITE);
                    tPaint.setTextSize(35);
                    canvas.drawText(t.mTitle, startX+ 30, startY + iheight/3*2, tPaint);

                }
                // 좌표 값
                startY = startY + iheight + mSeparatorWidth;

            }


        }
        // 캘린더 내 paint 좌표 설정
        else {
            if (separatorWidthTotal > height)
                return;

            float iwidth = (width - separatorWidthTotal) / mTriangleAttr.size();

            float startX = getPaddingStart();
            float startY = getPaddingTop();

            for (TriangleAttr t : mTriangleAttr) {
                canvas.drawPath(getBackgroundPath(t, startX, startY, iwidth, height), t.mBackgroundPaint);
                canvas.drawPath(getTrianglePath(t, startX, startY, iwidth, height), t.mPaint);

                startX = startX + iwidth + mSeparatorWidth;
            }
        }
    }

    private int max(int val1, int val2) {
        return val1 > val2 ? val1 : val2;
    }

    private float min(float val1, float val2) {
        return val1 < val2 ? val1 : val2;
    }
    // 최대 경로 및 최대값을 받아오기 위한 작업
    private Path getTrianglePath(TriangleAttr t,
                                 float initX,
                                 float initY,
                                 float width,
                                 float height) {
        if (t.mTrianglePath == null) {
            t.mTrianglePath = new Path();
            Point p1, p2, p3;
            switch (t.mDirection) {
                case TOP_LEFT:
                    p1 = new Point((int)(initX), (int)(initY));
                    p2 = new Point((int)(initX + Math.min(width, height)), (int)(initY));
                    p3 = new Point((int)(initX), (int)(initY + Math.min(width, height)));
                    break;
                case TOP_RIGHT:
                    p1 = new Point((int)(initX + width), (int)(initY));
                    p2 = new Point((int)(initX + width - Math.min(width, height)), (int)(initY));
                    p3 = new Point((int)(initX + width), (int)(initY + Math.min(width, height)));
                    break;
                case BOTTOM_LEFT:
                    p1 = new Point((int)(initX), (int)(initY + height));
                    p2 = new Point((int)(initX - Math.min(width, height)), (int)(initY + height));
                    p3 = new Point((int)(initX), (int)(initY + height - Math.min(width, height)));
                    break;
                case BOTTOM_RIGHT:
                default:
                    p1 = new Point((int)(initX + width), (int)(initY + height));
                    p2 = new Point((int)(initX + width), (int)(initY + height - Math.min(width, height)));
                    p3 = new Point((int)(initX + width - Math.min(width, height)), (int)(initY + height));
            }
            t.mTrianglePath.moveTo(p1.x, p1.y);
            t.mTrianglePath.lineTo(p2.x, p2.y);
            t.mTrianglePath.lineTo(p3.x, p3.y);
        }
        return t.mTrianglePath;
    }
    // 배결 최대값 설정을 위한 작업
    private Path getBackgroundPath(TriangleAttr t,
                                   float initX,
                                   float initY,
                                   float width,
                                   float height) {
        if (t.mBackgroundPath == null) {
            t.mBackgroundPath = new Path();
            Point p1, p2, p3, p4;
            p1 = new Point((int) initX, (int) initY);
            p2 = new Point((int) initX, (int) (initY + height));
            p3 = new Point((int) (initX + width), (int) (initY + height));
            p4 = new Point((int) (initX + width), (int) initY);

            t.mBackgroundPath.moveTo(p1.x, p1.y);
            t.mBackgroundPath.lineTo(p2.x, p2.y);
            t.mBackgroundPath.lineTo(p3.x, p3.y);
            t.mBackgroundPath.lineTo(p4.x, p4.y);
        }
        return t.mBackgroundPath;
    }

    //
    // Direction
    //

    public enum Direction {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public enum ViewDirection {
        HORIZONTAL,
        VERTICAL
    }
}
