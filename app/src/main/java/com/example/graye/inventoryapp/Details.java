package com.example.graye.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.graye.inventoryapp.Data.InventoryContract;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class Details extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText productEditText;
    EditText quantityEditText;
    EditText priceEditText;
    boolean EditMode = false;
    Button btn;
    Uri Inv_Row_Uri;
    Boolean unSavedData = true;
    final static int CAMERA_RESQUEST_CODE = 1;
    ImageView im;
    byte[] byteArray;
    String email = "grayeslove@gmail.com";
    String subject = "Order Product";
    String body = "Welcome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);

        im = (ImageView) findViewById(R.id.image_view);
        Button imagebtn = (Button) findViewById(R.id.butten_camera);
        Long id = getIntent().getLongExtra("ID", -1);
        Button btnDelete = (Button) findViewById(R.id.butten_deletes);
        //for INC button
        Button btnInc = (Button) findViewById(R.id.inc_quantity);
        //for DEC button
        Button btnDec = (Button) findViewById(R.id.dec_quantity);
//for order button
        Button orderbtn = (Button) findViewById(R.id.butten_order);

        btnInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(quantityEditText.getText().toString());

                } catch (Exception e) {
                    e.getStackTrace();
                }
                if (Inv_Row_Uri != null) {

                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity + 1);
                    getContentResolver().update(Inv_Row_Uri, values, null, null);
                }
                quantityEditText.setText(quantity + 1 + "");
            }
        });
        btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(quantityEditText.getText().toString());

                } catch (Exception e) {
                    e.getStackTrace();
                }
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    if (Inv_Row_Uri != null) {
                        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity - 1);
                        getContentResolver().update(Inv_Row_Uri, values, null, null);
                    }
                    quantityEditText.setText(quantity - 1 + "");
                }
            }
        });
        orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, email);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No app found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (id != -1) {
            EditMode = true;

            Inv_Row_Uri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.INVENTORY_URI, id);

            getSupportLoaderManager().initLoader(2, null, this);
            btnDelete.setVisibility(View.VISIBLE);
        }
        productEditText = (EditText) findViewById(R.id.product_edit_text);
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        btn = (Button) findViewById(R.id.butten_save);

        if (btn != null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(productEditText.getText()) && !TextUtils.isEmpty(quantityEditText.getText()) && !TextUtils.isEmpty(priceEditText.getText()) && (EditMode || byteArray != null)) {

                        ContentValues values = new ContentValues();
                        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRODUCT, productEditText.getText().toString());
                        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantityEditText.getText().toString());
                        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, priceEditText.getText().toString());
                        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE, byteArray);

                        if (EditMode) {
                            getContentResolver().update(Inv_Row_Uri, values, null, null);
                        } else {
                            getContentResolver().insert(InventoryContract.InventoryEntry.INVENTORY_URI, values);
                        }
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.fill_strings_message, Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Details.this);
                builder.setMessage("Are you Sure ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContentResolver().delete(Inv_Row_Uri, null, null);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                unSavedData = true;
                return false;
            }
        };
        priceEditText.setOnTouchListener(listener);
        quantityEditText.setOnTouchListener(listener);
        priceEditText.setOnTouchListener(listener);

        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_RESQUEST_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Inv_Row_Uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToNext()) {
            String product = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRODUCT));
            productEditText.setText(product);
            String quantity = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY));
            quantityEditText.setText(quantity);
            String price = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE));
            priceEditText.setText(price);
            byte[] pic = cursor.getBlob(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE));
            if (pic != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
                im.setImageBitmap(bitmap);
            }
        }
        cursor.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onBackPressed() {
        if (unSavedData) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do You Want to Leave ?");
            builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Details.super.onBackPressed();

                }
            });
            builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("debug", resultCode + "");
        if (requestCode == CAMERA_RESQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            im.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            Log.e("debug", byteArray + "");

        }
    }
}
