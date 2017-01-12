package com.garyhu.radardemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.garyhu.radardemo.R;

/**
 * 作者： garyhu.
 * 时间： 2016/10/11.
 * 圆形图片
 */
public class CircleImageView extends ImageView {


    /**
     * 绘图的Paint
     */
    private Paint paint;
    /**
     * 圆角的半径
     */
    private int radius;
    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private Matrix matrix;
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader bitmapShader;
    /**
     * view的宽度
     */
    private int width;

    private String cropType;

    /**外圆的宽度*/
    private float outCircleWidth;
    /**外圆的颜色*/
    private int outCircleColor;
    private Paint outCirclePaint;
    /**内圆的宽度*/
    private float innerCircleWidth;
    /**内圆的颜色*/
    private int innerCircleColor;
    private Paint innerCirclePaint;

    public CircleImageView(Context context) {
        this(context,null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public void init(Context context,AttributeSet attrs){
        cropType = CropType.centerTop;
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
            outCircleWidth = typedArray.getDimension(R.styleable.CircleImageView_civ_outCircleWidth, dp2px(4));
            outCircleColor = typedArray.getColor(R.styleable.CircleImageView_civ_outCircleColor, Color.parseColor("#CCCCCC"));
            innerCircleWidth = typedArray.getDimension(R.styleable.CircleImageView_civ_innerCircleWidth, dp2px(4));
            innerCircleColor = typedArray.getColor(R.styleable.CircleImageView_civ_innerCircleColor, Color.parseColor("#CCCCCCCC"));
            int cropType = typedArray.getInteger(R.styleable.CircleImageView_civ_cropType, 0);
            if(cropType == 0 ){
                this.cropType = CropType.centerTop;
            }else if(cropType == 1 ){
                this.cropType = CropType.leftTop;
            }else if(cropType == 2){
                this.cropType = CropType.center;
            }
        }
        matrix = new Matrix();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        radius = (int) dp2px(10);
        outCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outCirclePaint.setColor(outCircleColor);
        outCirclePaint.setStrokeWidth(outCircleWidth);
		/*消除锯齿  */
        outCirclePaint.setAntiAlias(true);
		/*绘制空心圆  */
        outCirclePaint.setStyle(Paint.Style.STROKE);
        outCirclePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setColor(innerCircleColor);
        innerCirclePaint.setStrokeWidth(innerCircleWidth);
		/*消除锯齿  */
        innerCirclePaint.setAntiAlias(true);
		/*绘制空心圆  */
        innerCirclePaint.setStyle(Paint.Style.STROKE);
        innerCirclePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = Math.min(getMeasuredHeight(),getMeasuredWidth());

        radius = width/2;
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable()==null){
            return;
        }
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        initBitmapShadow();
        canvas.drawCircle(radius, radius, radius - outCircleWidth, paint);
        canvas.drawCircle(radius, radius, radius - outCircleWidth*0.5F, outCirclePaint);
        canvas.drawCircle(radius, radius, radius-outCircleWidth - innerCircleWidth*0.5F, innerCirclePaint);
    }

    public void initBitmapShadow(){
        Drawable drawable = getDrawable();
        if(drawable ==null){
            return;
        }

        Bitmap bmp = drawable2Bitmap(drawable);
        bitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
        float scale = width * 1.0f / bSize;
        matrix.setScale(scale, scale);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
    }

    /**
     * drawable转bitmap
     *处理不同情况下的图片显示
     * @param drawable
     * @return
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        drawable.setBounds(0, 0, w, h);
        if ((w >= h) && CropType.centerTop.equals(cropType)) {
            canvas.translate((h - w) * 0.5F, 0);
        } else if ((w >= h) && CropType.center.equals(cropType)) {
            canvas.translate((h - w) * 0.5F, 0);
        } else if ((w < h) && CropType.center.equals(cropType)) {
            canvas.translate(0, (w - h) * 0.5F);
        }
        drawable.draw(canvas);
        return bitmap;
    }

    public static final class CropType {
        public static final String leftTop = "起点在左上角";
        public static final String centerTop = "起点水平居中&垂直置顶";
        public static final String center = "起点在图片中心";
    }

    /**
     * 数据转换: dp---->px
     */
    private float dp2px(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
