package com.example.scaleapp.ui.server_list;

public class Item {
    private String product_id;
    private String product_name;
    private String product_weight;

    public Item(String product_id, String product_name, String product_weight) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_weight = product_weight;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_weight() {
        return product_weight;
    }
}
