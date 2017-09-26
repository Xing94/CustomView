package com.lucio.customutil.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 动态压力曲线图
 */
public class DynamicCurveView extends SurfaceView implements SurfaceHolder.Callback {

    //曲线画笔
    private Paint linePaint;
    //网格画笔
    private Paint griddingPaint;
    //虚线网格画笔
    private Paint hiddenPaint;
    //空间轴画笔
    private Paint axlePaint;
    //字体画笔
    private Paint textPaint;
    //数据点画笔
    private Paint pointPaint;
    //曲线绘制path
    private Path mPath;

    //曲线数据
    private float[] stressData;
    //之前的数据 两个数据的变化来控制曲线变化效果
    private float[] beforeData;

    //网格X线的数量
    private float xSum;
    //网格Y线的数量
    private float ySum;
    //取值显示范围
    private float valueRange;
    //数据点的半径
    private float radius;
    //视图的宽高度
    private float width;
    private float height;

    //最大值
    private int maxValue;

    //变化速度
    private int timeSpeed;
    //变化的时长
    private int time;

    // 坐标轴和网格bitmap缓存
    private Bitmap gridBitmapCache;

    private Thread drawThread;
    private Runnable runnable;

    //是否正在运行
    private boolean isRunning;

    private SurfaceHolder mHolder;


    public DynamicCurveView(Context context) {
        super(context);
        init();
    }

    public DynamicCurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        //设置surfaceView可以设置背景色
        this.setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2.0f);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#00c6d0"));

        griddingPaint = new Paint();
        griddingPaint.setStyle(Paint.Style.STROKE);
        griddingPaint.setStrokeWidth(1.0f);
        griddingPaint.setAntiAlias(true);
        griddingPaint.setAlpha(128);
        griddingPaint.setColor(Color.parseColor("#00c6d0"));

        hiddenPaint = new Paint();
        hiddenPaint.setStyle(Paint.Style.STROKE);
        hiddenPaint.setStrokeWidth(1.0f);
        hiddenPaint.setAntiAlias(true);
        hiddenPaint.setAlpha(25);
        hiddenPaint.setColor(Color.parseColor("#00c6d0"));

        axlePaint = new Paint();
        axlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        axlePaint.setStrokeWidth(2.0f);
        axlePaint.setAntiAlias(true);
        axlePaint.setColor(Color.parseColor("#00c6d0"));

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#00c6d0"));
        textPaint.setTextSize(18.0f);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(Color.parseColor("#00c6d0"));

        mPath = new Path();

        xSum = 6.0f;
        ySum = 12.0f;

        radius = 10.0f;
        maxValue = 100;
        valueRange = 0;

        width = getWidth();
        height = getHeight();

        stressData = new float[(int) ySum - 1];
        beforeData = new float[(int) ySum - 1];
        for (int i = 0; i < stressData.length; i++) {
            stressData[i] = 0.0f;
            beforeData[i] = 0.0f;
        }

        timeSpeed = 1;
        time = 10;

        isRunning = true;

        runnable = new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    synchronized (mHolder) {
                        Canvas canvas = mHolder.lockCanvas();
                        if (canvas == null) continue;
                        doDraw(canvas);
                        mHolder.unlockCanvasAndPost(canvas);
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        start();
        //重新创建：切换fragment时不会因为drawThread而错误

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        width = i1;
        height = i2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stop();
    }

    public void stop() {
        isRunning = false;
        timeSpeed = time;
        drawThread = new Thread();
        drawThread.start();
    }

    public void start() {
        timeSpeed = 1;
        isRunning = true;
        drawThread = new Thread(runnable);
        drawThread.start();
    }

    protected void doDraw(Canvas canvas) {
        //清空画布

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (valueRange == 0) {
            valueRange = getHeight() - getHeight() / (xSum + 1.0f) * 2.0f;
        }


        //绘制刻度值
        drawGridding(canvas);
        //绘制曲线
        drawBight(canvas);

    }

    //绘制网格和刻度值 为什么要写这么多呢：计算太多了，取出来少一点判断
    private void drawGridding(Canvas canvas) {
        if (gridBitmapCache == null) {
            gridBitmapCache = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(gridBitmapCache);
            textPaint.setTextSize(20.0f);
            hiddenPaint.setAlpha(25);

            drawAL(width / (ySum + 1.0f) + axlePaint.getStrokeWidth() / 2.0f, height - height / (xSum + 1.0f),
                    (width / (ySum + 1.0f)) + axlePaint.getStrokeWidth() / 2.0f, 10, tempCanvas, axlePaint);

            for (int j = 1; j < 11; j++) {
                float xx = width / (ySum + 1.0f) + width / (ySum + 1.0f) / 11 * j;
                tempCanvas.drawLine(xx, height / (xSum + 1.0f), xx, height - height / (xSum + 1.0f), hiddenPaint);
            }

            //绘制竖线网格 加上X轴的刻度值
            for (int i = 2; i < (ySum + 1); i++) {

                tempCanvas.drawText(String.valueOf(i - 1), width / (ySum + 1.0f) * i - textPaint.getTextSize() / 2.0f,
                        height - height / (xSum + 1.0f) + textPaint.getTextSize() + radius, textPaint);

                tempCanvas.drawLine(width / (ySum + 1.0f) * i, height / (xSum + 1.0f), width / (ySum + 1.0f)
                        * i, height - height / (xSum + 1.0f), griddingPaint);

                if (i != ySum) {
                    for (int j = 1; j < 11; j++) {
                        float xx = width / (ySum + 1.0f) * i + width / (ySum + 1.0f) / 11 * j;
                        tempCanvas.drawLine(xx, height / (xSum + 1.0f), xx, height - height / (xSum + 1.0f), hiddenPaint);
                    }
                }
            }

            drawAL(width / (ySum + 1.0f), height / (xSum + 1.0f) * xSum, width - 10, height / (xSum + 1.0f) * xSum, tempCanvas, axlePaint);

            //绘制横线网格 加上Y轴的刻度值
            for (int i = 1; i < xSum; i++) {

                tempCanvas.drawLine(width / (ySum + 1.0f), height / (xSum + 1.0f) * i,
                        width - width / (ySum + 1.0f), height / (xSum + 1.0f) * i, griddingPaint);
                tempCanvas.drawText(String.valueOf(((int) xSum - i) * maxValue / 5), width / (ySum + 1.0f) - textPaint.getTextSize() * 2,
                        height / (xSum + 1.0f) * i + textPaint.getTextSize() / 2, textPaint);

                if (i < xSum) {
                    for (int j = 1; j < 6; j++) {
                        float yy = height / (xSum + 1.0f) * i + height / (xSum + 1.0f) / 6.0f * j;
                        tempCanvas.drawLine(width / (ySum + 1.0f), yy, width - width / (ySum + 1.0f), yy, hiddenPaint);

                    }
                }
            }

            textPaint.setTextSize(24.0f);

        }
        canvas.drawBitmap(gridBitmapCache, 0, 0, null);
    }

    //绘制曲线
    private void drawBight(final Canvas canvas) {
        mPath.reset();

        if (timeSpeed < time) {
            float yValue;
            float nowValue = height - height / (xSum + 1.0f) - stressData[0] / maxValue * valueRange;
            float beforValue = height - height / (xSum + 1.0f) - beforeData[0] / maxValue * valueRange;

            if (stressData[0] > beforeData[0]) {
                yValue = beforValue - (beforValue - nowValue) / time * timeSpeed;
            } else {
                yValue = beforValue + (nowValue - beforValue) / time * timeSpeed;
            }

            //绘制曲线的起点
            mPath.moveTo(width / (ySum + 1) * 2, yValue);
            //绘制数据点的起点
            canvas.drawCircle(width / (ySum + 1.0f) * 2.0f, yValue, radius, pointPaint);

            //绘制曲线和数据点
            for (int i = 1; i < stressData.length; i++) {
                nowValue = height - height / (xSum + 1.0f) - stressData[i] / maxValue * valueRange;
                beforValue = height - height / (xSum + 1.0f) - beforeData[i] / maxValue * valueRange;

                if (stressData[0] > beforeData[0]) {
                    yValue = beforValue - (beforValue - nowValue) / time * timeSpeed;
                } else {
                    yValue = beforValue + (nowValue - beforValue) / time * timeSpeed;
                }

                mPath.lineTo(width / (ySum + 1.0f) * (i + 2.0f), yValue);

                canvas.drawCircle(width / (ySum + 1.0f) * (i + 2), yValue, radius, pointPaint);
            }

            timeSpeed++;
        } else {
            float yValue = height - height / (xSum + 1.0f) - stressData[0] / maxValue * valueRange;

            //绘制曲线的起点
            mPath.moveTo(width / (ySum + 1) * 2, yValue);
            //绘制数据点的起点
            canvas.drawCircle(width / (ySum + 1.0f) * 2.0f, yValue, radius, pointPaint);

            //绘制曲线和数据点
            for (int i = 1; i < stressData.length; i++) {
                yValue = height - height / (xSum + 1.0f) - stressData[i] / maxValue * valueRange;

                canvas.drawCircle(width / (ySum + 1.0f) * (i + 2), yValue, radius, pointPaint);
                mPath.lineTo(width / (ySum + 1.0f) * (i + 2.0f), yValue);
            }

            System.arraycopy(this.stressData, 0, beforeData, 0, this.stressData.length);
        }
        canvas.drawPath(mPath, linePaint);

    }

    /**
     * 画箭头
     */
    public void drawAL(float sx, float sy, float ex, float ey, Canvas myCanvas, Paint myPaint) {
        double H = 8; // 箭头高度
        double L = 3.5; // 底边的一半
        float x3 = 0;
        float y3 = 0;
        float x4 = 0;
        float y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        myCanvas.drawLine(sx, sy, ex, ey, myPaint);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        myCanvas.drawPath(triangle, myPaint);

    }

    // 计算
    public double[] rotateVec(float px, float py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }

        return mathstr;
    }

    //设置数据 需要运行在主线程中
    public void setDynamicCurveData(float[] stressData) {
        for (int i = 0; i < this.stressData.length; i++) {
            beforeData[i] = this.stressData[i];
            //控制最大值 暂时不要
            if (stressData[i] > maxValue) {
                this.stressData[i] = maxValue;
            } else {
                this.stressData[i] = stressData[i];
            }
//            this.stressData[i] = stressData[i];
        }
        timeSpeed = 1;

    }

    //按照分段设置数据
    public void setDynamicCurveData(int[] stressData) {
        for (int i = 0; i < this.stressData.length; i++) {
            beforeData[i] = this.stressData[i];
            //控制最大值 暂时不要
            if (stressData[i] * 10 > maxValue) {
                this.stressData[i] = maxValue / 10;
            } else {
                this.stressData[i] = stressData[i] * 10;
            }
        }
        timeSpeed = 1;

    }

    //设置数据 需要运行在主线程中
    public void setDynamicCurveData(int position, float stressData) {
        beforeData[position] = this.stressData[position];
        this.stressData[position] = stressData;
        timeSpeed = 1;
    }

    public int getStressData(int position) {
        return (int) this.stressData[position];
    }

}
