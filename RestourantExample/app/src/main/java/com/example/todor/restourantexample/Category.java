package com.example.todor.restourantexample;

import java.util.ArrayList;

/**
 * Created by todor on 17.07.14.
 */

public class Category {

    private String name;
    private String productsCount;
    private String thumbnail;
    private int id;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoductsCount() {
        return productsCount;
    }

    public void setProductsCount(String productsCount) {
        this.productsCount = productsCount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    ArrayList<Product> productList = new ArrayList<Product>();

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void addProduct(Product product) {
        productList.add(product);
    }
}
