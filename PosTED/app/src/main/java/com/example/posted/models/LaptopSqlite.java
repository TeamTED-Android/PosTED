package com.example.posted.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.example.posted.interfaces.Laptop;

public class LaptopSqlite implements Parcelable, Laptop {

    private int mId;
    private String mModel;
    private String mCapacity_ram;
    private String mCapacity_hdd;
    private String mProcessor_type;
    private String mVideo_card_type;
    private String mDisplay_size;
    private String mCurrency;
    private String mPrice;
    private String mImagePath;
    private String mImageName;

    public LaptopSqlite(String model,
                        String capacity_ram,
                        String capacity_hdd,
                        String processor_type,
                        String video_card_type,
                        String display_size,
                        String currency,
                        String price,
                        String imagePath,
                        String imageName) {
        this.mModel = model;
        this.mCapacity_ram = capacity_ram;
        this.mCapacity_hdd = capacity_hdd;
        this.mProcessor_type = processor_type;
        this.mVideo_card_type = video_card_type;
        this.mDisplay_size = display_size;
        this.mCurrency = currency;
        this.mPrice = price;
        this.mImagePath = imagePath;
        this.mImageName = imageName;
    }

    public LaptopSqlite() {

    }

    public LaptopSqlite(Parcel in) {
        this.mId = in.readInt();
        this.mModel = in.readString();
        this.mCapacity_ram = in.readString();
        this.mCapacity_hdd = in.readString();
        this.mProcessor_type = in.readString();
        this.mVideo_card_type = in.readString();
        this.mDisplay_size = in.readString();
        this.mCurrency = in.readString();
        this.mPrice = in.readString();
        this.mImagePath = in.readString();
        this.mImageName = in.readString();
    }

    public static final Creator<LaptopSqlite> CREATOR = new Creator<LaptopSqlite>() {
        @Override
        public LaptopSqlite createFromParcel(Parcel in) {
            return new LaptopSqlite(in);
        }

        @Override
        public LaptopSqlite[] newArray(int size) {
            return new LaptopSqlite[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mModel);
        dest.writeString(this.mCapacity_ram);
        dest.writeString(this.mCapacity_hdd);
        dest.writeString(this.mProcessor_type);
        dest.writeString(this.mVideo_card_type);
        dest.writeString(this.mDisplay_size);
        dest.writeString(this.mCurrency);
        dest.writeString(this.mPrice);
        dest.writeString(this.mImagePath);
        dest.writeString(this.mImageName);
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getModel() {
        return this.mModel;
    }

    public void setModel(String model) {
        this.mModel = model;
    }

    public String getCapacity_ram() {
        return this.mCapacity_ram;
    }

    public void setCapacity_ram(String capacity_ram) {
        this.mCapacity_ram = capacity_ram;
    }

    public String getCapacity_hdd() {
        return this.mCapacity_hdd;
    }

    public void setCapacity_hdd(String capacity_hdd) {
        this.mCapacity_hdd = capacity_hdd;
    }

    public String getProcessor_type() {
        return this.mProcessor_type;
    }

    public void setProcessor_type(String processor_type) {
        this.mProcessor_type = processor_type;
    }

    public String getVideo_card_type() {
        return this.mVideo_card_type;
    }

    public void setVideo_card_type(String video_card_type) {
        this.mVideo_card_type = video_card_type;
    }

    public String getDisplay_size() {
        return this.mDisplay_size;
    }

    public void setDisplay_size(String display_size) {
        this.mDisplay_size = display_size;
    }

    public String getCurrency() {
        return this.mCurrency;
    }

    public void setCurrency(String currency) {
        this.mCurrency = currency;
    }

    public String getPrice() {
        return this.mPrice;
    }

    public void setPrice(String price) {
        this.mPrice = price;
    }

    public String getImagePath() {
        return this.mImagePath;
    }

    public void setImagePath(String path) {
        this.mImagePath = path;
    }

    public String getImageName() {
        return this.mImageName;
    }

    public void setImageName(String mImageName) {
        this.mImageName = mImageName;
    }
}
