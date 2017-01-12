package com.garyhu.radardemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 作者： garyhu.
 * 时间： 2016/11/26.
 * 五角星图
 */

public class StarView extends View{

    private Paint paint;
    private Paint titlePaint;
    private int textSize = (int) dp2px(38);
    private int magin = (int) dp2px(15);
    private float radius;
    private int centerX,centerY;
    private int dataCount = 5;
    private float radian = (float)(Math.PI*2)/dataCount;

    private String[] titles = {"是","国","我","中","人"};

    public StarView(Context context) {
        this(context,null);
    }

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        titlePaint = new Paint();
        titlePaint.setStyle(Paint.Style.STROKE);
        titlePaint.setAntiAlias(true);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(textSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(w,h)/2*0.5f;
        centerX = w/2;
        centerY = h/2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStar(canvas);
        drawTitle(canvas);
    }

    public void drawStar(Canvas canvas){
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            if(i==0){
                path.moveTo(getPosition(i).x,getPosition(i).y);
            }else {
                path.lineTo(getPosition(i).x,getPosition(i).y);
            }
        }

        path.close();
        canvas.drawPath(path,paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path,paint);

    }

    public void drawTitle(Canvas canvas){
        for (int i = 0; i < dataCount; i++) {
            int x = getPosition(i,magin).x;
            int y = getPosition(i,magin).y;
            float titleWidth = titlePaint.measureText(titles[i]);
            if(i==0){
                y += getTextHeight(titlePaint)/2;
            }else if(i==1){
                x -= titleWidth;
                y += getTextHeight(titlePaint)/2;
            }else if(i==2){
                x -= titleWidth/2;
                y -= getTextHeight(titlePaint)/2;
            }else if(i==3){
                y += getTextHeight(titlePaint)/2;
            }else if(i==4){
                x -= titleWidth;
                y += getTextHeight(titlePaint)/2;
            }

            canvas.drawText(titles[i],x,y,titlePaint);
        }
    }

    public Point getPosition(int position){
        return getPosition(position,0);
    }

    public Point getPosition(int position,int margin){
        int x = 0;
        int y = 0;
        if(position==0){
            x = (int) (centerX+Math.cos(Math.PI/2-radian)*(radius+margin));
            y = (int) (centerY-Math.sin(Math.PI/2-radian)*(radius+margin));
        }else if(position==1){
            x = (int) (centerX-Math.sin(Math.PI-2*radian)*(radius+margin));
            y = (int) (centerY+Math.cos(Math.PI-2*radian)*(radius+margin));
        }else if(position==2){
            x = centerX;
            y = (int) (centerY-(radius+margin));
        }else if(position==3){
            x = (int) (centerX+Math.sin(Math.PI-2*radian)*(radius+margin));
            y = (int) (centerY+Math.cos(Math.PI-2*radian)*(radius+margin));
        }else if(position==4){
            x = (int) (centerX-Math.cos(Math.PI/2-radian)*(radius+margin));
            y = (int) (centerY-Math.sin(Math.PI/2-radian)*(radius+margin));
        }
        return new Point(x,y);
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, getResources().getDisplayMetrics());
    }

    private int getTextHeight(Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent-fontMetrics.ascent);
    }
}
