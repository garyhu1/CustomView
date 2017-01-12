package com.garyhu.radardemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * 时间： 2016/11/25.
 * 雷达图
 */

public class RadarView extends View {

    /**
     * 雷达基础图形画笔
     */
    private Paint outsidePaint;

    /**
     * 标题画笔
     */
    private Paint titlePaint;

    /**
     * 内部占有比例覆盖的画笔
     */
    private Paint inCaverPaint;

    /**
     * 文字的画笔
     */
    private Paint textPaint;

    /**
     * 图片的画笔
     */
    private Paint imgPaint;

    /**
     * 雷达中心点
     */
    private int centerX,centerY;

    /**
     * 雷达多边形与文字的外边距
     */
    private int radarMagin = (int) dp2px(15);

    /**
     * 雷达半径
     */
    private float radius;

    private int dataCount = 5;

    /**
     * 角度
     */
    private float radian = (float)(Math.PI*2)/dataCount;
    private String[] titles = {"履约能力","信用历史","人脉关系","行为偏好","身份特质"};
    private int[] icons = {R.drawable.credit_card,R.drawable.clock,
            R.drawable.connection,R.drawable.heart,R.drawable.crown};
    private float maxValue = 190;
    private float[] dataValues = {180,170,160,170,180};
    private int textSize = (int) dp2px(30);
    private int scoreSize = (int) dp2px(28);


    public RadarView(Context context) {
        this(context,null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
        init();
    }

    /**
     * 初始化自定义属性
     * @param context
     * @param attrs
     */
    public void initAttrs(Context context, AttributeSet attrs){
        if(attrs==null){
            return;
        }
    }

    /**
     * 初始化配置
     */
    public void init(){
        outsidePaint = new Paint();
        outsidePaint.setAntiAlias(true);
        outsidePaint.setColor(Color.BLUE);
        outsidePaint.setStyle(Paint.Style.STROKE);

        inCaverPaint = new Paint();
        inCaverPaint.setAntiAlias(true);
        inCaverPaint.setAlpha(0);
//        inCaverPaint.setARGB(0,0,0,0);
        inCaverPaint.setColor(Color.GRAY);
        inCaverPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(scoreSize);
        textPaint.setStyle(Paint.Style.STROKE);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(textSize);
        titlePaint.setStyle(Paint.Style.STROKE);
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
        drawPolygon(canvas);
        drawLines(canvas);
        drawCover(canvas);
        drawScore(canvas);
        drawTitle(canvas);
        drawIcon(canvas);
    }

    /**
     * 画多边形
     */
    public void drawPolygon(Canvas canvas){
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            if(i == 0){
                path.moveTo(getPosition(i).x,getPosition(i).y);
            }else {
                path.lineTo(getPosition(i).x,getPosition(i).y);
            }
        }
        path.close();
        canvas.drawPath(path,outsidePaint);
    }

    public void drawLines(Canvas canvas){
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            path.reset();
            int x = getPosition(i).x;
            int y = getPosition(i).y;
            path.moveTo(centerX,centerY);
            path.lineTo(x,y);
            canvas.drawPath(path,outsidePaint);
        }
    }

    public void drawCover(Canvas canvas){
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            float precent = dataValues[i]/maxValue;
            int x = getPosition(i,0,precent).x;
            int y = getPosition(i,0,precent).y;
            if(i==0){
                path.moveTo(x,y);
            }else {
                path.lineTo(x,y);
            }
        }
        path.close();
        canvas.drawPath(path,inCaverPaint);

        inCaverPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path,inCaverPaint);
    }

    public void drawScore(Canvas canvas){
        float score = 0;
        for (int i = 0; i < dataCount; i++) {
            score += dataValues[i];
        }
        String ss = score+"";
        float v = textPaint.measureText(ss);
        canvas.drawText(score+"",centerX-v/2,centerY+scoreSize/2,textPaint);
    }

    public void drawTitle(Canvas canvas){
        for (int i = 0; i < dataCount; i++) {
            int x = getPosition(i,radarMagin,1).x;
            int y = getPosition(i,radarMagin,1).y;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),icons[i]);
            int iconHeight = bitmap.getHeight();
            float titleWidth = titlePaint.measureText(titles[i]);
            if(i==1){
                y += iconHeight/2+textSize/2;
            }else if(i==2){
                x -= titleWidth*1.2f;
                y += iconHeight/2+textSize/2;
            }else if(i==3){
                x -= titleWidth;
                y += textSize/2;
            }else if(i==4){
                x -= titleWidth/2;
            }else if(i==0){
                y += textSize/2;
            }

            canvas.drawText(titles[i],x,y,titlePaint);
        }
    }

    public void drawIcon(Canvas canvas){
        for (int i = 0; i < dataCount; i++) {
            int x = getPosition(i,radarMagin,1).x;
            int y = getPosition(i,radarMagin,1).y;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),icons[i]);
            int iconWidth = bitmap.getWidth();
            int iconHeight = bitmap.getHeight();
            float titleWidth = titlePaint.measureText(titles[i]);
            if(i==0){
                y += textSize/2- iconHeight -getTextHeight(titlePaint);
                x += (titleWidth-iconWidth)/2;
            }else if(i==1){
                x += (titleWidth-iconWidth)/2;
                y -= iconHeight/2+textSize/2;
            }else if(i==2){
                x -= titleWidth*1.2f - (titleWidth-iconWidth)/2;
                y -= iconHeight/2+textSize/2;
            }else if(i==3){
                x -= titleWidth-(titleWidth-iconWidth)/2;
                y -= textSize/2 + iconHeight;
            }else if(i==4){
                x -=  titleWidth/2 -(titleWidth-iconWidth)/2;
                y -= getTextHeight(titlePaint)+iconHeight;
            }

            canvas.drawBitmap(bitmap,x,y,titlePaint);
        }
    }

    public Point getPosition(int position){
        int x = getPosition(position,0,1).x;
        int y = getPosition(position,0,1).y;
        return new Point(x,y);
    }

    public Point getPosition(int position,int margin,float precent){
        int x = 0;
        int y = 0;
        if(position == 0){
            x = (int) (centerX + (Math.sin(radian)*(radius+margin))*precent);
            y = (int) (centerY-(Math.cos(radian)*(radius+margin))*precent);
        }else if(position == 1){
            x = (int) (centerX + (Math.sin(radian/2)*(radius+margin))*precent);
            y = (int) (centerY+(Math.cos(radian/2)*(radius+margin))*precent);
        }if(position == 2){
            x = (int) (centerX - (Math.sin(radian/2)*(radius+margin))*precent);
            y = (int) (centerY + (Math.cos(radian/2)*(radius+margin))*precent);
        }else if(position == 3){
            x = (int) (centerX - (Math.sin(radian)*(radius+margin))*precent);
            y = (int) (centerY-(Math.cos(radian)*(radius+margin))*precent);
        }else if(position == 4){
            x = centerX ;
            y = (int) (centerY-(radius+margin)*precent);
        }
        return new Point(x,y);
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, getResources().getDisplayMetrics());
    }

    public int getTextHeight(Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent-fontMetrics.ascent);
    }
}
