package com.example.scaleapp.Model;

import com.example.scaleapp.ui.server_list.Item;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

public class ConnectionManager {
    OkHttpClient client = new OkHttpClient();

    Item[] dataItem;

    Gson gson = new Gson();

    public boolean sendDataToBase(String name, Integer weight) {
        String result = uploadData(name, weight);
        if (result.contains("200")) {
            return true;
        }
        return false;
    }


    public ArrayList<Item> getItemList() {
        ArrayList<Item> list = new ArrayList<>();
        for (Item item : getItemFromJson()) {
            list.add(item);
        }
        return list;
    }

    private Item[] getItemFromJson() {
        try {
            dataItem = gson.fromJson(downloadItemList(), Item[].class);
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }
        return dataItem;
    }


    private String uploadData(String name, Integer weight) {
        return doGetRequest(new DataBaseLink(name, weight).getUploadURL());
    }


    private String downloadItemList() {
        return doGetRequest(DataBaseLink.PRODUCT_LIST_URL);
    }


    public String doGetRequest(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException io) {
            io.fillInStackTrace();
        }

        return "-";
    }
}