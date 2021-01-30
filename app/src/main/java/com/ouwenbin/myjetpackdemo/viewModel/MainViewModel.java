package com.ouwenbin.myjetpackdemo.viewModel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.AndroidViewModel;

/*
* 只要使用androidx，就等于导入JerPack库
* 作用：把MainActivity的数据Model给抽取出来
* 层级关系：Model里装载着LiveData 和 业务逻辑
* 注意：
    1.如果不需要Context上下文环境的可以继承ViewModel类，如果需要Context上下文环境就需要继承AndroidViewModel类
* */

public class MainViewModel extends AndroidViewModel {

    //LiveData:对数据的感应（当数据发生改变时，以观察者设计模式，来让我们的数据发生改变，进而人UI发生改变）
    /*
    * LiveData里的方法:
        1.num.setValue("值")：设置num的值
        2.num.getValue():获取num的值
    */
    private MutableLiveData<String> num;

    private Context context;
    //继承AndroidViewModel类，必须要实现的构造方法
    public MainViewModel(@NonNull Application application) {
        super(application);
        this.context=application.getApplicationContext();//获取上下文环境
    }

    //把数据返回去（暴露给外部）
    public MutableLiveData<String> getNum(){
        if (num == null){
            num =new MutableLiveData<>();//初始化LiveData
            num.setValue("0");//初始化数据
        }
        return num;
    }

    //改变TextView的值，需要传入参数
    public void appendNumber(String number){
        clear();
        num.setValue(num.getValue() + number);
    }

    //+1
    int i=0;
    public void Jia(){

        clear();
        i=i+1;
        num.setValue(String.valueOf(i));

        showToast();
    }

    //-1
    public void jian(){
        clear();
        i=i-1;
        num.setValue(String.valueOf(i));

        showToast();
    }

    public void showToast(){
        Toast.makeText(context, num.getValue(), Toast.LENGTH_SHORT).show();
    }

    public void clear(){
        num.setValue("");
    }
}
