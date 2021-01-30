package com.ouwenbin.myjetpackdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ouwenbin.myjetpackdemo.viewModel.LiveDataViewMode;

public class LiveDataActivity extends AppCompatActivity {

    private LiveDataViewMode liveDataViewMode;
    private TextView tv_LiveData;
    private Button btn_LiveData1,btn_LiveData2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);

        initView();
        initListener();
    }

    private void initView() {
        tv_LiveData=findViewById(R.id.tv_LiveData);
        btn_LiveData1=findViewById(R.id.btn_LiveData1);
        btn_LiveData2=findViewById(R.id.btn_LiveData2);

        //初始化ViewMode
        liveDataViewMode = new ViewModelProvider(this).get(LiveDataViewMode.class);

        //给ViewMode添加观察者（作用：感应数据的变化，当数据发生变化时就会调用onChanged()方法）
        liveDataViewMode.getNum().observe(this, new Observer<Integer>() {
            @Override
            //当数据发送改变是就会调用这个方法
            public void onChanged(Integer integer) {
                tv_LiveData.setText(String.valueOf(integer));
            }
        });
    }

    private void initListener() {

        btn_LiveData1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataViewMode.addnum(1);
            }
        });

        btn_LiveData2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataViewMode.addnum(2);
            }
        });

    }
}
