package com.tom.photo;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PHOTO = 900;
    private static final String TAG = PhotoActivity.class.getSimpleName();
    private String selectedImagePath;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        image = (ImageView) findViewById(R.id.image);
    }

    public void pick(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            pickPhoto();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 110);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 110 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            pickPhoto();
        }
    }

    public void pickPhoto(){
        Intent pick = new Intent();
        pick.setType("image/*");
        pick.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(pick, REQUEST_PICK_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_PHOTO){
            Uri originUri = data.getData();
            try {
                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap(getContentResolver(), originUri);
                final int takeFlags = data.getFlags() &
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //noinspection WrongConstant
                getContentResolver().takePersistableUriPermission(originUri, takeFlags);
                String id = originUri.getLastPathSegment().split(":")[1];
                final String[] imagesColumns = {MediaStore.Images.Media.DATA};
                final String orderBy = null;
                Uri uri = getUri();
                selectedImagePath = null;
                Cursor cusror = managedQuery(uri, imagesColumns,
                        MediaStore.Images.Media._ID +"=" +id, null, orderBy);
                if (cusror.moveToFirst()){
                    selectedImagePath =
                            cusror.getString(cusror.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                Log.d(TAG, selectedImagePath);
                image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private Uri getUri(){
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    public void upload(View view){
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storage.child(new Date().getTime()+"");
        try {
            FileInputStream fis = new FileInputStream(selectedImagePath);
            imagesRef.putStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
