package com.ouwenbin.myjetpackdemo.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveDataViewMode extends ViewModel {

    private MutableLiveData<Integer> num;

    public MutableLiveData<Integer> getNum() {

        if (num == null){
            num =new MutableLiveData<>();//初始化LiveData
            num.setValue(0);//初始化数据
        }

        return num;
    }

    public void addnum (int i){
        num.setValue(num.getValue()+i);
    }

}
