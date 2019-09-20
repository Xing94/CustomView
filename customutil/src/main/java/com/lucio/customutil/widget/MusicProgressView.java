package com.lucio.customutil.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Random;

public class MusicProgressView extends LinearLayout {

    public MusicProgressView(Context context) {
        super(context);
        init(context);
    }

    public MusicProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void init(final Context context) {

        this.post(new Runnable() {
            @Override
            public void run() {

                //移除所有view
                removeAllViews();

                //使用随机数字
                Random random = new Random();

                int sum = 300;

                int widthSum = 0;

                int lineWidth = getWidth() / sum;

                int lineMinHeight = getHeight() / 10;

                int randomBound = 20;

                while (widthSum < getWidth() - lineWidth * 2) {

                    int randomInt = random.nextInt(randomBound);
                    LinearLayout.LayoutParams layoutParams =
                            new LayoutParams(lineWidth, lineMinHeight + randomInt * (getHeight() - lineMinHeight) / randomBound);
                    View lineView = new View(context);

                    layoutParams.setMargins(getChildCount() == 0 ? lineWidth : 0, 0, lineWidth, 0);

                    lineView.setLayoutParams(layoutParams);

                    lineView.setBackgroundColor(Color.parseColor("#FF0000"));

                    addView(lineView);

                    widthSum = widthSum + lineWidth * 2;

                }
            }
        });

    }

}
