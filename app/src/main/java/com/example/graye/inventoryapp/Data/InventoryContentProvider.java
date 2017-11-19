package com.example.graye.inventoryapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class InventoryContentProvider extends ContentProvider {
    InventoryDBHelper dbHelper;
    static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        mUriMatcher.addURI("com.example.android.inventoryapp", "InventoryStaff", 1);
        mUriMatcher.addURI("com.example.android.inventoryapp", "InventoryStaff/#", 2);
        mUriMatcher.addURI("com.example.android.inventoryapp", "product/#", 3);

    }


    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int code = mUriMatcher.match(uri);
        Cursor cursor;
        switch (code) {
            case 1:
                cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;

            case 2:
                String whereClause = "_id=?";
                String[] WhereParams = new String[]{ContentUris.parseId(uri) + ""};
                cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME, strings, whereClause, WhereParams, null, null, s1);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int code = mUriMatcher.match(uri);
        if (code == 1) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Long RowId = db.insertOrThrow(InventoryContract.InventoryEntry.TABLE_NAME, null, contentValues);
            if (RowId == -1)
                throw new IllegalArgumentException("ERROR");

            else
                getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(InventoryContract.InventoryEntry.INVENTORY_URI, RowId);
        } else
            throw new IllegalArgumentException("Couldn't resolve URI" + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int code = mUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        switch (code) {
            case 1:
                affectedRows = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, s, strings);
                break;
            case 2:
                String where = "_id=?";
                String[] whereArgs = {ContentUris.parseId(uri) + ""};
                affectedRows = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri" + uri);

        }
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int code = mUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        switch (code) {
            case 1:
                affectedRows = db.update(InventoryContract.InventoryEntry.TABLE_NAME, contentValues, s, strings);
                break;

            case 2:
                String where = "_id=?";
                String[] whereArgs = new String[]{ContentUris.parseId(uri) + ""};
                affectedRows = db.update(InventoryContract.InventoryEntry.TABLE_NAME, contentValues, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri" + uri);
        }
        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }
}
