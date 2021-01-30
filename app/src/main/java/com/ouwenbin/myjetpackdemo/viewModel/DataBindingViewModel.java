package com.ouwenbin.myjetpackdemo.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class DataBindingViewModel extends ViewModel {

    private SavedStateHandle handle;
    private static final String KEY_NUMBER = "number";

    public DataBindingViewModel(SavedStateHandle handle) {
        this.handle = handle;
    }

    public MutableLiveData<Integer> getNum() {

        /*
            SavedStateHandle 类包含键值对映射应有的方法：
            get(String key):根据Key获取该值
            contains(String key):根据Key判断是否包含该值
            remove(String key)：根据Key删除该值
            set(String key, T value)：设置某个值
            keys()
        */

        if (!handle.contains(KEY_NUMBER)) {
            handle.set(KEY_NUMBER, 0);
        }

        return handle.getLiveData(KEY_NUMBER);
    }

    public void addnum (int i){

        getNum().setValue(getNum().getValue() + i);

    }

}
