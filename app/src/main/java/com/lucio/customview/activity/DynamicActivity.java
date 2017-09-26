package com.lucio.customview.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lucio.customutil.widget.DynamicCurveView;
import com.lucio.customview.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DynamicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);

        final DynamicCurveView dynamicCurveView = (DynamicCurveView) findViewById(R.id.dynamic_view);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dynamicCurveView.setDynamicCurveData(getPoint());
                    }
                });
            }
        }, 1000, 2000);
    }

    /**
     * 生成随机数据 临时用，可删除
     */
    public static float[] getPoint() {
        float[] points = new float[11];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Random().nextInt(100);
        }
        return points;
    }
}
