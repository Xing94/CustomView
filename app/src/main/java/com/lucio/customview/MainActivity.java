package com.lucio.customview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lucio.customview.activity.DirectionActivity;
import com.lucio.customview.activity.DynamicActivity;
import com.lucio.customview.activity.EcgActivity;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 绑定事件卸载layout布局当中了
     * @param view 视图
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ecg:
                startActivity(new Intent(MainActivity.this, EcgActivity.class));
                break;
            case R.id.btn_dynamic:
                startActivity(new Intent(MainActivity.this, DynamicActivity.class));
                break;
            case R.id.btn_direction:
                startActivity(new Intent(MainActivity.this, DirectionActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
