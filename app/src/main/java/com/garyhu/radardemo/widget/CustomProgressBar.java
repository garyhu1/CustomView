package com.garyhu.radardemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.garyhu.radardemo.R;


/**
 * 作者： garyhu.
 * 时间： 2016/10/22.
 * 自定义进度条
 */
public class CustomProgressBar extends View {

    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 画TextView的画笔
     */
    private Paint tvPaint;
    private float textSize = dipToPx(10);
    /**
     * 第一个圆环颜色
     */
    private int firstColor;
    /**
     * 第二个圆环颜色
     */
    private int secondColor;
    /**
     * 圆环宽度
     */
    private int circleWidth;
    /**
     * 进度速度
     */
    private int progressSpeed;
    /**
     * 当前进度
     */
    private int mProgress;
    /**
     * 是否下一个
     */
    private boolean isNext;

    public CustomProgressBar(Context context) {
        this(context,null);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int h = getMeasuredHeight();
        int height = h+dipToPx(120);
        int w = getScreenWidth();

        setMeasuredDimension(w,height);

    }

    /**
     * 初始化参数
     * @param context 上下文
     * @param attrs 自定义参数
     */
    public void init(Context context,AttributeSet attrs){
        if(attrs == null){
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.CustomProgressBar_firstColor:
                    firstColor = a.getInt(R.styleable.CustomProgressBar_firstColor, Color.BLACK);
                    break;
                case R.styleable.CustomProgressBar_secondColor:
                    secondColor = a.getInt(R.styleable.CustomProgressBar_secondColor,Color.RED);
                    break;
                case R.styleable.CustomProgressBar_circleWidth:
                    circleWidth = a.getInt(R.styleable.CustomProgressBar_circleWidth,
                            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,20,
                                    context.getResources().getDisplayMetrics()));//默认20
                    break;
                case R.styleable.CustomProgressBar_progressSpeed:
                    progressSpeed = a.getInt(R.styleable.CustomProgressBar_progressSpeed,20);
                    break;
            }
        }
        a.recycle();
        mPaint = new Paint();
        new Thread(){
            @Override
            public void run() {
                while(true){
                    mProgress++;
                    if(mProgress == 360){
                        mProgress = 0;
                        if(!isNext){
                            isNext = true;
                        }else {
                            isNext = false;
                        }
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(progressSpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        circleWidth = 40;
        int center = (getHeight()-dipToPx(120))/2;//圆环圆心
        int radius = center-circleWidth;//圆环半径
        mPaint.setStrokeWidth(circleWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        tvPaint = new Paint();
        tvPaint.setTextSize(textSize);
        tvPaint.setColor(Color.BLACK);
        tvPaint.setTextAlign(Paint.Align.CENTER);
        // 用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);

        canvas.drawText("10小时23分钟",center,center+textSize/3,tvPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(center,center,radius,mPaint);
        mPaint.setColor(secondColor);
        canvas.drawArc(oval,180,240,false,mPaint);

        canvas.drawText("21:39:23",center+center/2,center*2+textSize/3,tvPaint);

        //循环画圆环
//        if(!isNext){
//            mPaint.setColor(firstColor);
//            canvas.drawCircle(center,center,radius,mPaint);
//            mPaint.setColor(secondColor);
//            canvas.drawArc(oval,-90,mProgress,false,mPaint);
//        }else {
//            mPaint.setColor(secondColor);
//            canvas.drawCircle(center,center,radius,mPaint);
//            mPaint.setColor(firstColor);
//            canvas.drawArc(oval,-90,mProgress,false,mPaint);
//        }
    }

    /**
     * dip 转换成px
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int)(dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 获取屏幕的宽度
     */
    private int getScreenWidth(){
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
