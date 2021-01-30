package com.ouwenbin.myjetpackdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ouwenbin.myjetpackdemo.databinding.ActivityMainBinding;
import com.ouwenbin.myjetpackdemo.viewModel.MainViewModel;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    int num = 0;
    private TextView tv_num;
    private Button btn_jia;

    //获取res文件夹下定义的全局资源
    //String key =getApplication().getResources().getString(R.string.app_name);

    //DataBind后续函数的名字，是根据 布局里面指定的 标记变化而变化 例如：setMainData()
    private ActivityMainBinding binding;//定义DataBinding（系统自动生成的类，格式：布局XML名 + Binding）
    private MainViewModel mainViewModel;//定义自定义ViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //传统的形式(使用binding后，initView()方法就无效了)
        //initView();

        /*---------------JerPack改造后的------------------------------*/
        //给DataBinding绑定布局
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //解决this报错：https://blog.csdn.net/weixin_40981751/article/details/107633771
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);//初始化ViewMode
        //设置DataBinding的数据来源
        binding.setMainData(mainViewModel);
        //建立观察者（布局）和ViewModel的感应
        binding.setLifecycleOwner(this);


    }

    private void initView() {
        setContentView(R.layout.activity_main);
        tv_num = findViewById(R.id.tv_num);
        btn_jia = findViewById(R.id.btn_jia);
        btn_jia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = num + 1;
                tv_num.setText(String.valueOf(num));
            }
        });
    }
}
