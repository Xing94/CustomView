package com.lucio.customutil.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * 渐变色背景
 */

public class ShadeBgView extends View {

    //画笔
    private Paint mPaint;
    //点的位置
    private float[] points;
    //控制比例
    private int[] pointHeight;
    //渐变色集合
    private int[] colors;
    //渐变色位置
    private float[] positions;
    //x点的位置集合
    private float[] pointX;

    public ShadeBgView(Context context) {
        super(context);
        init();
    }

    public ShadeBgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShadeBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        colors = new int[]{0xFF00c6d0, 0x00FFFFFF};
        positions = new float[]{0.0f, 1.0f};

        points = new float[11];
        pointHeight = new int[11];
        pointX = new float[11];

    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < pointHeight.length; i++) {
            points[i] = (getHeight() - 20) / 10.0f * (10 - pointHeight[i]) + 10;
        }

        if (pointX[0] == 0) {
            for (int i = 0; i < pointX.length; i++) {
                pointX[i] = 16 + 110 * i;
            }
        }

        //渐变色着色器
        LinearGradient shader = new LinearGradient(0, 0, 0, getHeight(), colors, positions, Shader.TileMode.CLAMP);

        mPaint.setShader(shader);

        Path mPath = new Path();
        mPath.moveTo(16.0f, points[0]);

        float gap = (getWidth() - 32) / (points.length - 1);

        for (int i = 1; i < points.length; i++) {
            mPath.cubicTo(gap * i - gap / 2 + 16, points[i - 1], gap * i - gap / 2 + 16, points[i], gap * i + 16, points[i]);
        }

        mPath.lineTo(getWidth(), getHeight());
        mPath.lineTo(16.0f, getHeight());
        mPath.lineTo(16.0f, points[0]);
        canvas.drawPath(mPath, mPaint);
    }

    //设置数据
    public void setPoints(int[] pointHeight) {

        for (int i = 0; i < pointHeight.length; i++) {
            pointHeight[i] = pointHeight[i] > 10 ? 10 : pointHeight[i];
            if (i >= pointHeight.length) {
                return;
            }
            this.pointHeight[i] = pointHeight[i];
        }
        invalidate();
    }

    //设置数据
    public void setPoints(int position, int height) {
        if (position < pointHeight.length) {
            height = height > 10 ? 10 : height;
            pointHeight[position] = height;
            invalidate();
        }
    }
}
