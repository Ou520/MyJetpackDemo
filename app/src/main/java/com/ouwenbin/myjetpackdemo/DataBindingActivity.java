package com.ouwenbin.myjetpackdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;


import com.ouwenbin.myjetpackdemo.databinding.ActivityDataBindingBinding;
import com.ouwenbin.myjetpackdemo.viewModel.DataBindingViewModel;

public class DataBindingActivity extends AppCompatActivity {

    private DataBindingViewModel model;//定义自定义ViewModel
    private ActivityDataBindingBinding binding;//定义DataBinding（系统自动生成的类，格式：布局XML名 + Binding）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {

        //给DataBinding绑定布局
        binding = DataBindingUtil.setContentView(this,R.layout.activity_data_binding);
        //解决this报错：https://blog.csdn.net/weixin_40981751/article/details/107633771
        model = new ViewModelProvider(this).get(DataBindingViewModel.class);//初始化ViewMode
        //设置DataBinding的数据来源
        binding.setData(model);
        //建立观察者（布局）和ViewModel的感应
        binding.setLifecycleOwner(this);
    }
}
