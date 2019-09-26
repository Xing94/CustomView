package com.lucio.customutil.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.appwidget.AppWidgetHost;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lucio.customutil.R;

import java.util.ArrayList;
import java.util.List;

public class DirectionView extends View {

    //折线走势画笔
    private Paint directionPaint;
    //虚线定位画笔
    private Paint locationPaint;
    //错误提示文字画笔
    private Paint hintTextPaint;

    //折线走势路径
    private Path directionPath;

    //走势路径数据
    private List<DirectionBean> directionBeanList;

    //点击到的横坐标
    private float touchX;
    //是否点击到当前view
    private boolean isTouch;

    //宽高度
    private float width;
    //宽高度
    private float height;

    //错误提示颜色
    private int hintTextColor;
    //走势图折线颜色
    private int directionColor;
    //定位线颜色
    private int locationColor;

    //是否出错
    private boolean isError;

    //上下空余高度
    private int emptyHeight;

    //触摸事件
    private DirectionTouchListener directionTouchListener;

    public DirectionView(Context context) {
        super(context);
        init();
    }

    public DirectionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DirectionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //最早能在onLayout生命周期内获取到视图宽高度
        if (width == 0) {
            width = getWidth();
            height = getHeight();
        }
    }

    private void init() {
        //设置为true之后才能接收ACTION_MOVE等手势
        setClickable(true);

        emptyHeight = 20;

        //提示颜色
        hintTextColor = Color.parseColor("#5AC8FA");
        //折线颜色
        directionColor = Color.parseColor("#5AC8FA");
        //定位颜色
        locationColor = Color.parseColor("#DCDCDC");

        //走势折线画笔
        directionPaint = new Paint();
        directionPaint.setStyle(Paint.Style.STROKE);
        directionPaint.setStrokeWidth(8f);
        directionPaint.setAntiAlias(true);
        directionPaint.setColor(directionColor);
        //连接的外边缘以圆弧的方式相交
        directionPaint.setStrokeJoin(Paint.Join.ROUND);
        //线条结束处绘制一个半圆
        directionPaint.setStrokeCap(Paint.Cap.ROUND);

        //定位线画笔
        locationPaint = new Paint();
        locationPaint.setStyle(Paint.Style.STROKE);
        locationPaint.setStrokeWidth(3f);
        locationPaint.setAntiAlias(true);
        locationPaint.setColor(locationColor);

        //提示文字画笔
        hintTextPaint = new Paint();
        hintTextPaint.setStyle(Paint.Style.STROKE);
        hintTextPaint.setTextSize(50);
        hintTextPaint.setAntiAlias(true);
        hintTextPaint.setColor(hintTextColor);

        //绘制圆环
//        cirPaint

        directionPath = new Path();

        directionBeanList = new ArrayList<>();

        width = getWidth();
        height = getHeight();

        isTouch = false;

        touchX = -1;

        isError = false;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        directionPath.reset();

        if (directionBeanList != null && directionBeanList.size() > 0) {
            isError = false;
            if (isTouch) {
                drawLocationLine(canvas);
            }
            drawDirection(canvas);
        } else {
            drawErrorHint(canvas);
        }
    }

    /**
     * 绘制错误情况视图
     *
     * @param canvas 画布
     */
    private void drawErrorHint(Canvas canvas) {
        isError = true;
        String hintText = "加载数据出错，点击重试";
        canvas.drawText(hintText, width / 2 - hintTextPaint.getTextSize() * hintText.length() / 2,
                height / 2, hintTextPaint);
    }

    /**
     * 绘制走势折线图
     *
     * @param canvas 画布
     */
    private void drawDirection(Canvas canvas) {

        //是否已经回调输出过数据
        boolean isGetData = false;
        //定位圆点的坐标
        float[] junctionIndex = {0, 0};

        float tempHeight = height - emptyHeight * 2;

        for (int i = 0; i < directionBeanList.size(); i++) {

            //绘制走势折线图
            if (i == 0) {
                float x = 0;
                //设置数据点对应的x坐标
                directionBeanList.get(i).setX(x);
                directionPath.moveTo(x, emptyHeight + (1 - directionBeanList.get(i).getY()) * tempHeight);

            } else {
                float x = width / (directionBeanList.size() - 1) * i;
                directionBeanList.get(i).setX(x);

                directionPath.lineTo(x, emptyHeight + (1 - directionBeanList.get(i).getY()) * tempHeight);

            }

            //绘制每个数据点的圆点
//            canvas.drawCircle(directionBeanList.get(i).getX(), (1-directionBeanList.get(i).getY()) * height, 3, directionPaint);

            //输出定位线附近的的数据
            if (directionTouchListener != null) {
                if (touchX < 0) {
                    //当没有
                    junctionIndex[0] = 0;
                    junctionIndex[1] = 0;
                    //当没有点击的时候，输出数据为null
                    directionTouchListener.lineTouch(null);
                } else {
                    //是否已经获取到数据：避免重复获取
                    if (!isGetData) {

                        //定位圆点的x坐标
                        junctionIndex[0] = touchX;

                        //触摸的x坐标与数据点x坐标相等的时候
                        if (touchX == directionBeanList.get(i).getX()) {
                            isGetData = true;

                            junctionIndex[1] = directionBeanList.get(i).getY();

                            directionTouchListener.lineTouch(directionBeanList.get(i));
                        } else if (touchX < directionBeanList.get(i).getX() && i > 0) {
                            //触摸的x坐标与数据点x坐标不相等的时候：获取触摸点前一个x坐标的数据
                            //设置定位圆点的y坐标：计算前后两点之间的比例，通过touchX获取touchY
                            junctionIndex[1] = directionBeanList.get(i - 1).getY() +
                                    (directionBeanList.get(i).getY() - directionBeanList.get(i - 1).getY()) *
                                            (touchX - directionBeanList.get(i - 1).getX()) /
                                            (directionBeanList.get(i).getX() - directionBeanList.get(i - 1).getX());
                            //只获取一次数据
                            isGetData = true;
                            //数据回调
                            directionTouchListener.lineTouch(new DirectionBean(touchX, junctionIndex[1]));
                        }
                    }
                }
            }
        }


        //增加绘制动画
//        ObjectAnimator animator=new ObjectAnimator();
//        animator.setDuration(2000);
//        animator.setFloatValues(0, 1f);
//        animator.setTarget(this);
//        animator.start();

        //绘制走势折线图
        canvas.drawPath(directionPath, directionPaint);

        if (isTouch && touchX >= 0) {
            //需要设置当前view的layerType，setShadowLayer才会对drawCircle作用
            setLayerType(LAYER_TYPE_SOFTWARE, null);

            int arcWidth = 30;

            Paint arcPaint = new Paint();
            arcPaint.setStrokeWidth(arcWidth);
            arcPaint.setStyle(Paint.Style.FILL);
            arcPaint.setColor(Color.parseColor("#FFFFFF"));
            arcPaint.setAntiAlias(true);
            //绘制阴影
            arcPaint.setShadowLayer(20f, 0, 0, Color.parseColor("#32000000"));

            //绘制定位圆点：定位圆点为定位线和走势折线图的交点
            canvas.drawCircle(junctionIndex[0], emptyHeight + (1 - junctionIndex[1]) * tempHeight, arcWidth, arcPaint);

            arcPaint.setShadowLayer(0f, 0, 0, Color.parseColor("#FF0000"));
            arcPaint.setColor(Color.parseColor("#5AC8FA"));

            canvas.drawCircle(junctionIndex[0], emptyHeight + (1 - junctionIndex[1]) * tempHeight, arcWidth / 2, arcPaint);

        }

    }

    /**
     * 绘制定位线
     *
     * @param canvas
     */
    private void drawLocationLine(Canvas canvas) {
        canvas.drawLine(touchX, 0, touchX, getHeight(), locationPaint);
    }


    /**
     * @param event
     * @return
     * @see #setClickable(boolean)
     * <p>
     * view只有在设置clickable为true的时候，才会接收到ACTION_MOVE、ACTION_UP等事件，
     * 否则只能接收ACTION_DOWN
     * <p>
     * getX()：获取的是以被点击的控件左上角为坐标原点的横坐标
     * getRawX()：获取的是以屏幕左上角为坐标原点的横坐标
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isError) {
                    touchX = event.getX();
                    isTouch = true;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isError) {
                    touchX = event.getX();
                    isTouch = true;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isError) {
                    directionTouchListener.lineTouch(null);
                    touchX = -1;
                    isTouch = false;
                    invalidate();
                } else {
                    if (directionTouchListener != null) {
                        directionTouchListener.lineTouch(null);
                        directionTouchListener.errorTouch();
                    }
                }
                break;

        }
        return super.onTouchEvent(event);
    }

    public void setDirectionBeanList(List<DirectionBean> directionBeanList) {
        this.directionBeanList = directionBeanList;
        invalidate();
    }

    public void setDirectionTouchListener(DirectionTouchListener directionTouchListener) {
        this.directionTouchListener = directionTouchListener;
    }

    public void setHintTextColor(int hintTextColor) {
        this.hintTextColor = hintTextColor;
    }

    public void setDirectionColor(int directionColor) {
        this.directionColor = directionColor;
    }

    public void setLocationColor(int locationColor) {
        this.locationColor = locationColor;
    }

    public static class DirectionBean {

        private float x;
        private float y;

        public DirectionBean() {
        }

        public DirectionBean(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    public interface DirectionTouchListener {

        //错误触摸事件：数据错误时为刷新
        void errorTouch();

        //定位线获取数据回调
        void lineTouch(DirectionBean directionBean);
    }

}
