package com.example.graye.inventoryapp.Data;


import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    public static final String StringUri = "content://com.example.android.inventoryapp";
    public static final Uri BASE_URI = Uri.parse(StringUri);

    public final static class InventoryEntry implements BaseColumns {

        public final static String TABLE_NAME = "InventoryStaff";
        public final static String COLUMN_INVENTORY_ID = BaseColumns._ID;
        public final static String COLUMN_INVENTORY_PRODUCT = "product";
        public final static String COLUMN_INVENTORY_QUANTITY = "quantity";
        public final static String COLUMN_INVENTORY_PRICE = "price";
        public final static String COLUMN_INVENTORY_IMAGE = "image";


        public final static Uri INVENTORY_URI = BASE_URI.withAppendedPath(BASE_URI, TABLE_NAME);


    }
}
