package com.example.matjeusz.opencv;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class WyborRysowania extends AppCompatActivity {


    private static final int SELECTED_PICTURE=1;
    long addres;
    public Bitmap selected;
    Mat matt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wybor_rysowania);
    }





    public void btnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECTED_PICTURE);

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECTED_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    selected = BitmapFactory.decodeFile(filePath);
                    selected = selected.copy(Bitmap.Config.ARGB_8888, true);
                    ByteArrayOutputStream bStream = new
                            ByteArrayOutputStream();
                    selected.compress(Bitmap.CompressFormat.PNG,100,bStream);
                    byte[] byteArray = bStream.toByteArray();
                    Intent intent=new Intent(this,ZdjecieZgalerii.class);
                    intent.putExtra("bitmap",byteArray);
                    startActivity(intent);
                    finish();

                }
                break;
            default:
                break;


        }


    }





    public void wzor(View view) {
        Intent wzor = new Intent(this, MainActivity.class);
        //Intent aparat = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // uruchamianie aparatu
        this.startActivity(wzor);

    }






}
