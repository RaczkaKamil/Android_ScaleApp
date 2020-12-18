package com.example.scaleapp.ui.ScaleSearcher;


import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;


public class ScaleSearcherViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Scale>> mScale;

    private ArrayList<Scale> scaleList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ScaleSearcherViewModel() {
        mText = new MutableLiveData<>();
        mScale = new MutableLiveData<>();
        mText.setValue("Scale list: ");
        mScale.setValue(scaleList);
    }


    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<Scale>> getScale() {
        return mScale;
    }


}