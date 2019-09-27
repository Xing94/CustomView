package com.lucio.customview.activity;

import android.annotation.SuppressLint;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lucio.customutil.widget.DirectionView;
import com.lucio.customview.R;
import com.lucio.customview.testclass.TestBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DirectionActivity extends AppCompatActivity {

    private float downY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        final NestedScrollView mParentScroll = findViewById(R.id.scroll_view);

        final DirectionView<TestBean> directionView = findViewById(R.id.view_direction);

        final TextView tvDataShow = findViewById(R.id.tv_data_show);

        directionView.setDirectionTouchListener(new DirectionView.DirectionTouchListener<TestBean>() {
            @Override
            public void errorTouch() {
                Random random = new Random();

                List<DirectionView.DirectionBean<TestBean>> directionBeanList = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    DirectionView.DirectionBean<TestBean> directionBean = new DirectionView.DirectionBean<>();
                    directionBean.setY(random.nextFloat());
                    directionBean.setData(new TestBean(directionBean.getY() + ""));
                    directionBeanList.add(directionBean);
                }

                directionView.setDirectionBeanList(directionBeanList);
            }

            @Override
            public void getDirectionData(DirectionView.DirectionBean<TestBean> directionBean) {
                if (directionBean == null) {
                    tvDataShow.setText("当前数据：空");
                } else {
                    tvDataShow.setText(new StringBuffer("当前数据：").append(directionBean.getData().getData())
                            .append("\n当前Y坐标：").append(directionBean.getY()));
                }
            }
        });

        directionView.setScrollView(mParentScroll);

    }
}
