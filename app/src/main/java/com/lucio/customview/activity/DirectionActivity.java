package com.lucio.customview.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lucio.customutil.widget.DirectionView;
import com.lucio.customview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DirectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        final DirectionView directionView = findViewById(R.id.view_direction);

        final TextView tvDataShow = findViewById(R.id.tv_data_show);

        directionView.setDirectionTouchListener(new DirectionView.DirectionTouchListener() {
            @Override
            public void errorTouch() {
                Random random = new Random();

                List<DirectionView.DirectionBean> directionBeanList = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    DirectionView.DirectionBean directionBean = new DirectionView.DirectionBean();
                    directionBean.setY(random.nextFloat());
                    directionBeanList.add(directionBean);
                }

                directionView.setDirectionBeanList(directionBeanList);
            }

            @Override
            public void lineTouch(DirectionView.DirectionBean directionBean) {
                if (directionBean == null) {
                    tvDataShow.setText("当前数据：空");
                } else {
                    tvDataShow.setText("当前数据：" + directionBean.getY());
                }
            }
        });

    }
}
