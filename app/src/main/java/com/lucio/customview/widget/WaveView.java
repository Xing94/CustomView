package com.lucio.customview.widget;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 波动图/心电图
 * 三种图形样式：贝塞尔曲线、心电图、矩形图
 */
public class WaveView extends SurfaceView implements SurfaceHolder.Callback {

    //绘制的线程
    private Thread drawThread;
    //判断线程是否运行
    private boolean isRunning;
    //线的绘制画笔
    private Paint linePaint;
    //波动点右边X坐标
    private List<Float> pointXData;
    //波动点高度
    private List<Float> waveData;
    //师徒的高度和宽度
    private int width;
    private int height;
    //移动速度
    private float xSpeed;
    //波动点的长度
    private float waveWidth;
    //Y轴，绘制的直线Y点
    private int yAxle;
    //波动点之间的间隔
    private float waveGap;

    private Path mPath;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //设置surfaceView可以设置背景色
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        width = getWidth();
        height = getHeight();
        yAxle = height / 2;
        waveGap = width / 20;

        mPath = new Path();
        waveWidth = 300.0f;
        xSpeed = 5;

        isRunning = true;
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(10.0f);

        waveData = new ArrayList<>();
        pointXData = new ArrayList<>();

        final SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        drawThread = new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    Canvas canvas;
                    synchronized (holder) {
                        canvas = holder.lockCanvas();
                        try {
                            sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (canvas != null) {
                            doDraw(canvas);
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        };
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRunning = true;
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        width = i1;
        height = i2;
        yAxle = height / 2;
        waveGap = width / 20;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }

    private void doDraw(Canvas canvas) {
        if (waveData.size() > 0) {
            if (pointXData.get(0) > width + waveWidth) {
                pointXData.remove(0);
                waveData.remove(0);
            }
            for (int i = 0; i < pointXData.size(); i++) {
                pointXData.set(i, pointXData.get(i) + xSpeed);
                //设置波动点之间的间隔
                if (i < pointXData.size() - 1) {
                    if (pointXData.get(i + 1) > pointXData.get(i) - xSpeed - waveWidth - waveGap) {
                        pointXData.set(i + 1, pointXData.get(i) - xSpeed - waveWidth - waveGap);
                    }
                }

            }
        }

        //清空画布
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (pointXData.size() > 0) {
            //画开头的直线
            canvas.drawLine(0, yAxle, pointXData.get(pointXData.size() - 1) - waveWidth, yAxle, linePaint);
            //画结束的直线
            canvas.drawLine(pointXData.get(0), yAxle, width, yAxle, linePaint);
        } else {
            //如果没有波动点，就直接画一条直线
            canvas.drawLine(0, yAxle, width, yAxle, linePaint);
        }

        for (int i = 0; i < pointXData.size(); i++) {
            //画波动点
//            drawWave(canvas, i);

            drawBezier(canvas, i);

        }
    }

    /**
     * 画波动点 矩形 向上突出
     * @param canvas 画布
     * @param waveIndex 波动点的位置
     */
    private void drawWave(Canvas canvas, int waveIndex) {
        mPath.reset();
        mPath.moveTo(pointXData.get(waveIndex), yAxle + linePaint.getStrokeWidth() / 2);
        mPath.lineTo(pointXData.get(waveIndex), yAxle - waveData.get(waveIndex));
        mPath.lineTo(pointXData.get(waveIndex) - waveWidth, yAxle - waveData.get(waveIndex));
        mPath.lineTo(pointXData.get(waveIndex) - waveWidth, yAxle + linePaint.getStrokeWidth() / 2);

        canvas.drawPath(mPath, linePaint);

        //用drawLine，去缺角
        if (waveIndex < pointXData.size() - 1) {
            canvas.drawLine(pointXData.get(waveIndex) - waveWidth, yAxle, pointXData.get(waveIndex + 1), yAxle, linePaint);
        }
    }

    /**
     * 绘制贝塞尔曲线
     * 从右往左画
     *
     * @param canvas 画布
     */
    private void drawBezier(Canvas canvas, int waveIndex) {
//        if(waveData.get(waveIndex)>400){
//            linePaint.setColor(Color.RED);
//        }else{
//            linePaint.setColor(Color.BLUE);
//        }

        mPath.reset();
        mPath.moveTo(pointXData.get(waveIndex), yAxle - linePaint.getStrokeWidth() / 2);
        //cubicTo 右边的拐点 左边的拐点 左边的点
        mPath.cubicTo(pointXData.get(waveIndex) - waveWidth / 4 - waveWidth / 5, yAxle + waveData.get(waveIndex),
                pointXData.get(waveIndex) - waveWidth / 4 * 3 + waveWidth / 5, yAxle - waveData.get(waveIndex),
                pointXData.get(waveIndex) - waveWidth, yAxle + linePaint.getStrokeWidth() / 2
        );

        canvas.drawPath(mPath, linePaint);

        //用drawLine，去缺角
        if (waveIndex < pointXData.size() - 1) {
            canvas.drawLine(pointXData.get(waveIndex) - waveWidth, yAxle, pointXData.get(waveIndex + 1), yAxle, linePaint);
        }
    }

    /**
     * 绘制心电图曲线
     * 从右往左画
     *
     * @param canvas 画布
     */
    private void drawEcg(Canvas canvas, int waveIndex) {
        mPath.reset();
        mPath.moveTo(pointXData.get(waveIndex), yAxle + linePaint.getStrokeWidth() / 2);


        mPath.lineTo(pointXData.get(waveIndex) - waveWidth, yAxle + linePaint.getStrokeWidth() / 2);

        canvas.drawPath(mPath, linePaint);

        //用drawLine，去缺角
        if (waveIndex < pointXData.size() - 1) {
            canvas.drawLine(pointXData.get(waveIndex) - waveWidth, yAxle, pointXData.get(waveIndex + 1), yAxle, linePaint);
        }
    }

    //波动
    public void wave(float point) {
        if (point > 0) {
            waveData.add(point);
            if (pointXData.size() > 0) {
                if (pointXData.get(pointXData.size() - 1) < waveWidth - waveGap) {
                    pointXData.add(pointXData.get(pointXData.size() - 1) - waveWidth - waveGap);
                } else {
                    pointXData.add(-xSpeed * 20);
                }
            } else {
                //设置波动点出现位置：0.02秒后出现，优化动画显示效果
                pointXData.add(-xSpeed * 20);
            }

        }
    }
}
