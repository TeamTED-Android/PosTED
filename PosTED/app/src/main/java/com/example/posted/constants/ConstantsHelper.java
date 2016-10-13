package com.example.posted.constants;


public class ConstantsHelper {

    public static final String DB_NAME = "pos_ted.db";
    public static final int DB_VERSION = 2;
    //Admin username
    public static final String ADMIN_USERNAME = "admin@admin.com";
    //Users table
    public static final String USERS_TABLE_NAME = "users";
    public static final String USERNAME_COLUMN = "username";
    public static final String PASSWORD_COLUMN = "password";
    //Laptops table
    public static final String LAPTOPS_TABLE_NAME = "laptops";
    public static final String ID_COLUMN = "id";
    public static final String MODEL_COLUMN = "model";
    public static final String RAM_COLUMN = "capacity_ram";
    public static final String HDD_COLUMN = "capacity_hdd";
    public static final String PROCESSOR_COLUMN = "processor_type";
    public static final String VIDEO_CARD_COLUMN = "video_card_type";
    public static final String DISPLAY_COLUMN = "display_size";
    public static final String CURRENCY_COLUMN = "currency";
    public static final String PRICE_COLUMN = "price";
    public static final String IMAGE_PATH_COLUMN = "image_path";
    public static final String IMAGE_NAME_COLUMN = "image_name";
    //Admin added laptops table
    public static final String ADMIN_ADDED_LAPTOPS_TABLE_NAME = "added_laptops";

    //Admin added laptops table
    public static final String ADMIN_REMOVED_LAPTOPS_TABLE_NAME = "removed_laptops";

    //Current order table
    public static final String CURRENT_ORDERS_LAPTOPS_TABLE_NAME = "current_order";

    //Admin orders table
    public static final String ADMIN_ORDERS_LAPTOPS_TABLE_NAME = "admin_orders";
    public static final String USER_COLUMN = "user";

    public static final String LAPTOP_FRAGMENT_PARCELABLE_KEY = "current_laptop";
    public static final String FROM_WHERE_IS_INVOKED_KEY = "invocation";

    public static final String IMAGE_DIRECTORY_PATH = "imageDir";
    public static final String NO_IMAGE_TAG = "No image!";

    public static final int CAMERA_REQUESTS = 0;

    //Broadcasts
    public static final String BROADCAST_START_LOADING = "com.example.posted.start";
    public static final String BROADCAST_END_LOADING = "com.example.posted.end";

    //Send mail constants
    public static final String MESSAGE_TYPE = "message/rfc822";

    // Images
    public static final int DESIRED_IMAGE_BOUND = 192;

    //view pager constants
    public static final String IS_ADD_LIST = "add_list";
    public static final String IS_REMOVE_LIST = "remove_list";
    public static final String IS_CARD_LIST = "card_list";

    public static final String USER = "user";
    public static final String ADMIN = "admin";

    //Kinvey collections
    public static final String KINVEY_LAPTOPS_TABLE_NAME = "laptops";
    public static final String KINVEY_ORDERS_TABLE_NAME = "orders";

    public static final String OVERVIEW_LAPTOPS_COLLECTION = "overview_laptops";
    public static final String OVERVIEW_ORDERS_COLLECTION = "overview_orders";
    public static final String COLLECTION_CONSTANT = "collection";
}
