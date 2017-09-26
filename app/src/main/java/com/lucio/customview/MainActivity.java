package com.lucio.customview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lucio.customview.activity.DynamicActivity;
import com.lucio.customview.activity.EcgActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEcg = (Button) findViewById(R.id.btn_ecg);

        btnEcg.setOnClickListener(this);

        findViewById(R.id.btn_dynamic).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ecg:
                startActivity(new Intent(MainActivity.this, EcgActivity.class));
                break;
            case R.id.btn_dynamic:
                startActivity(new Intent(MainActivity.this, DynamicActivity.class));
                break;
        }
    }
}
