package com.nightssky.datatrendgraph;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nightssky.datatrendgraph.widget.DataTrendView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DataTrendView data_trend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        data_trend = (DataTrendView) findViewById(R.id.data_trend);

        // 随机数模拟数据
        List<Integer> dataList = new ArrayList();
//        dataList.add(160000);
//        dataList.add(30000);
//        dataList.add(120000);
//        dataList.add(50000);
//        dataList.add(140000);
//        dataList.add(110000);
//        dataList.add(20000);
        for (int i = 0; i < 7; i ++) {
            dataList.add((int) (Math.random()*160000));
        }
            data_trend.setData(dataList);

    }


}
