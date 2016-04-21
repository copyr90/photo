package com.example.tester.new_2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/4/10.
 */
public class ClipView extends View {

    /**
     * 边框距左右边界距离，用于调整边框长度
     */
    public static int BORDERDISTANCE = 0;

    /**
     * 裁剪长宽比
     */
    public static float ASPECTRATIO = 1;

    private Paint mPaint;

    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        // 边框长度
        float borderLength = (width - BORDERDISTANCE *2) * ASPECTRATIO;

        mPaint.setColor(0xaa000000);

        // 以下绘制透明暗色区域
        // top
        canvas.drawRect(0, 0, width, (height - borderLength) / 2, mPaint);
        // bottom
        canvas.drawRect(0, (height + borderLength) / 2, width, height, mPaint);
        // left
        canvas.drawRect(0, (height - borderLength) / 2, BORDERDISTANCE,
                (height + borderLength) / 2, mPaint);
        // right
        canvas.drawRect(width - BORDERDISTANCE, (height - borderLength) / 2, width,
                (height + borderLength) / 2, mPaint);

        // 以下绘制边框线
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(2.0f);
        // top
        canvas.drawLine(BORDERDISTANCE, (height - borderLength) / 2, width - BORDERDISTANCE, (height - borderLength) / 2, mPaint);
        // bottom
        canvas.drawLine(BORDERDISTANCE, (height + borderLength) / 2, width - BORDERDISTANCE, (height + borderLength) / 2, mPaint);
        // left
        canvas.drawLine(BORDERDISTANCE, (height - borderLength) / 2, BORDERDISTANCE, (height + borderLength) / 2, mPaint);
        // right
        canvas.drawLine(width - BORDERDISTANCE, (height - borderLength) / 2, width - BORDERDISTANCE, (height + borderLength) / 2, mPaint);
    }

    /**
     * 设置裁剪区域距离左右的距离
     * @param distance
     */
    public void setBorderDistance(int distance){
        BORDERDISTANCE = distance;
        invalidate();
    }

    /**
     * 设置长宽比
     * @param aspectRatio
     */
    public void setAspectRatio(float aspectRatio){
        ASPECTRATIO = aspectRatio;
        invalidate();
    }
}