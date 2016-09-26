package com.example.todor.restourantexample;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by todor on 21.07.14.
 */
public class Product implements Parcelable {

    private String name;
    private String description;
    private String price;
    private String thumbnail;
    private int count = 1;
    private int id;

    public Product(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(thumbnail);
    }

    public Product(Parcel parcel) {
        this.name = parcel.readString();
        this.description = parcel.readString();
        this.price = parcel.readString();
        this.thumbnail = parcel.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
