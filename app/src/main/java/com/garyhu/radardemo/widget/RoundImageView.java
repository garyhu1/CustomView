package com.garyhu.radardemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.garyhu.radardemo.R;


/**
 * 作者： garyhu.
 * 时间： 2016/10/21.
 */
public class RoundImageView extends ImageView {

    private static final int CIRCLE_TYPE = 0;//圆形图片
    private static final int ROUND_TYPE = 1;//圆角图片

    private Paint mPaint;//绘图的paint
    private BitmapShader bitmapShader;
    private Matrix matrix;

    private int type;//图片类型
    private int mWidth;//view的宽度
    private int mRadius;//图形的半径
    private int mBorderRadius;//圆角半径
    private RectF mRectF;

    private static final int BODER_RADIUS_DEFAULT = 10;//圆角的默认大小


    public RoundImageView(Context context) {
        this(context,null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        if(attrs==null){
            return;
        }
        matrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

        mBorderRadius = a.getInt(R.styleable.RoundImageView_borderRadius, (int)TypedValue.applyDimension(TypedValue.
                COMPLEX_UNIT_DIP,BODER_RADIUS_DEFAULT,context.getResources().getDisplayMetrics()));
        type = a.getInt(R.styleable.RoundImageView_type,0);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(type == CIRCLE_TYPE){
            mWidth = Math.min(getMeasuredHeight(),getMeasuredWidth());
            mRadius = mWidth/2;
            setMeasuredDimension(mWidth,mWidth);
        }

    }

    /**
     * 初始化BitmapShader
     */
    private void setUpShader()
    {
        Drawable drawable = getDrawable();
        if (drawable == null)
        {
            return;
        }

        Bitmap bmp = drawable2Bitmap(drawable);
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        bitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == CIRCLE_TYPE)
        {
            // 拿到bitmap宽或高的小值
            int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
            scale = mWidth * 1.0f / bSize;

        } else if (type == ROUND_TYPE)
        {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight()
                    * 1.0f / bmp.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        matrix.setScale(scale, scale);
        // 设置变换矩阵
        bitmapShader.setLocalMatrix(matrix);
        // 设置shader
        mPaint.setShader(bitmapShader);
    }


    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawable2Bitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() == null){
            return;
        }

        setUpShader();

        if(type == CIRCLE_TYPE){
            canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);
        }else {
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(type == ROUND_TYPE)
            mRectF = new RectF(0,0,getWidth(),getHeight());
    }

}


