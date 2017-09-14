package com.lucio.customview.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lucio.customview.R;
import com.lucio.customview.widget.WaveView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 心电图activity
 */
public class EcgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ((WaveView) findViewById(R.id.ecg_view)).wave(new Random().nextInt(200) + 300);
            }
        };
        timer.schedule(task, 0, 2000);
    }
}
