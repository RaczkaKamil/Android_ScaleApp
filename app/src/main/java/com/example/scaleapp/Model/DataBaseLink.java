package com.example.scaleapp.Model;


public class DataBaseLink {
    public static String PRODUCT_LIST_URL = "http://resom.pl/Scale/index.php?request=getData";


    private String UploadURL;

    public DataBaseLink(String name, int weiht) {
        this.UploadURL = "http://resom.pl/Scale/index.php?request=postData&name=" + name + "&weiht=" + weiht;
    }

    public String getUploadURL() {
        return UploadURL;
    }
}