package com.example.graye.inventoryapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graye.inventoryapp.Data.InventoryContract;

public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_view, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView productTextView = (TextView) view.findViewById(R.id.product_text_view);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quanrity_text_view);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
        ImageView imageTextView = (ImageView) view.findViewById(R.id.image_show_onScreen);
        //for sale button
        Button btnsale = (Button) view.findViewById(R.id.button_sale);
        String product = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRODUCT));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE));
        byte[] imageData = cursor.getBlob(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE));
        productTextView.setText(product);
        quantityTextView.setText(quantity + "");
        priceTextView.setText(price + "");
        if (imageData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            imageTextView.setImageBitmap(bitmap);
        }
        int id = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_ID));
        final Uri Inv_Row_Uri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.INVENTORY_URI, id);

        btnsale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity - 1);
                    context.getContentResolver().update(Inv_Row_Uri, values, null, null);
                }

            }
        });



    }
}
