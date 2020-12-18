package com.example.scaleapp.ui.server_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ServerListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ServerListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Server View: ");
    }

    public LiveData<String> getText() {
        return mText;
    }
}