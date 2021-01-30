package com.ouwenbin.myjetpackdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ouwenbin.myjetpackdemo.viewModel.MyViewModel;

import org.w3c.dom.Text;

public class ViewModelActivity extends AppCompatActivity {

    private MyViewModel myViewModel;
    private TextView tv_ViewModel;
    private Button btn_ViewModel1,btn_ViewModel2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_model);

        initView();
        initListener();

    }


    private void initView() {

        tv_ViewModel=findViewById(R.id.tv_ViewModel);
        btn_ViewModel1=findViewById(R.id.btn_ViewModel1);
        btn_ViewModel2=findViewById(R.id.btn_ViewModel2);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);//初始化ViewMode
        tv_ViewModel.setText(String.valueOf(myViewModel.num));
    }

    private void initListener() {
        btn_ViewModel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewModel.num++;
                tv_ViewModel.setText(String.valueOf(myViewModel.num));
            }
        });

        btn_ViewModel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewModel.num+=2;
                tv_ViewModel.setText(String.valueOf(myViewModel.num));
            }
        });
    }
}
