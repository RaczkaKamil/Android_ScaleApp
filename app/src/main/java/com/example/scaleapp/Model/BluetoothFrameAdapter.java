package com.example.scaleapp.Model;

import java.util.ArrayList;

public class BluetoothFrameAdapter {
    private ArrayList<Integer> arrayList;


    public BluetoothFrameAdapter() {
        arrayList = new ArrayList<>();
    }

    public void setMeasureObiectList(Integer value) {
        if (getSize() < 2) {
            arrayList.add(value);
        }
    }

    public int getSize() {
        return arrayList.size();
    }

    public void clear() {
        arrayList.clear();
    }

    public int getMeasure() {
        int result;
        result = (arrayList.get(0) * 256) + arrayList.get(1);
        return result;
    }

}
