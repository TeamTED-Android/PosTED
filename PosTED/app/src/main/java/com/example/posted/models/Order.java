package com.example.posted.models;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

public class Order extends GenericJson {

    @Key("_id")
    private String id;

    @Key("_kmd")
    private KinveyMetaData meta;

    @Key("_acl")
    private KinveyMetaData.AccessControlList acl;

    @Key
    private String model;

    @Key
    private String capacity_ram;

    @Key
    private String capacity_hdd;

    @Key
    private String processor_type;

    @Key
    private String video_card_type;

    @Key
    private String display_size;

    @Key
    private String currency;

    @Key
    private String price;

    @Key
    private String user;

    public Order() {

    }

    public Order(String model,
                 String capacity_ram,
                 String capacity_hdd,
                 String processor_type,
                 String video_card_type,
                 String display_size,
                 String currency,
                 String price,
                 String user) {
        this.model = model;
        this.capacity_ram = capacity_ram;
        this.capacity_hdd = capacity_hdd;
        this.processor_type = processor_type;
        this.video_card_type = video_card_type;
        this.display_size = display_size;
        this.currency = currency;
        this.price = price;
        this.user = user;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCapacity_ram() {
        return this.capacity_ram;
    }

    public void setCapacity_ram(String capacity_ram) {
        this.capacity_ram = capacity_ram;
    }

    public String getCapacity_hdd() {
        return this.capacity_hdd;
    }

    public void setCapacity_hdd(String capacity_hdd) {
        this.capacity_hdd = capacity_hdd;
    }

    public String getProcessor_type() {
        return this.processor_type;
    }

    public void setProcessor_type(String processor_type) {
        this.processor_type = processor_type;
    }

    public String getVideo_card_type() {
        return this.video_card_type;
    }

    public void setVideo_card_type(String video_card_type) {
        this.video_card_type = video_card_type;
    }

    public String getDisplay_size() {
        return this.display_size;
    }

    public void setDisplay_size(String display_size) {
        this.display_size = display_size;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
