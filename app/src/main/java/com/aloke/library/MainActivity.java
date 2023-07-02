package com.aloke.library;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aloke.libraries.cropper.CropImage;
import com.aloke.libraries.cropper.CropImageView;
import com.aloke.libraries.imagematch.ImageCheck;
import com.aloke.libraries.imagematch.mobilefacenet.MobileFaceNet;
import com.aloke.libraries.imagematch.mtcnn.MTCNN;
import com.aloke.libraries.signaturepad.SilkySignaturePad;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
private ImageView img,img2;
private Button btn;
private SilkySignaturePad spads;
    private MTCNN mtcnn = null;
    private MobileFaceNet mfn = null;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
//spads=findViewById( R.id.spad );
context=this;
        btn=findViewById( R.id.button2 );
        img=findViewById( R.id.imageView1 );
        Drawable myDrawable = getResources().getDrawable(R.drawable.scr);
        img.setImageDrawable(myDrawable);
        img2=findViewById(R.id.imageView);
        img2.setImageDrawable(myDrawable);

        try{
            mtcnn = new MTCNN(context);
            mfn = new MobileFaceNet(context);

        }catch(Exception ex){
            Log.e("ERRORS",ex.getMessage().toString());
        }


        btn.setOnClickListener(view -> {
//            Bitmap bt1=((BitmapDrawable) img.getDrawable()).getBitmap();
//            Bitmap bt2=((BitmapDrawable) img2.getDrawable()).getBitmap();
//            try {
//                var xy= ImageCheck.IsSame(bt1,bt2,mtcnn,mfn);
//                Log.i("Mathc",String.valueOf(xy));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            Intent int1 = new Intent(this, ActivityFaceCheck.class);
            startActivity(int1);
        });

      //  img.setImageResource(R.drawable.abcd);
//        btn.setOnClickListener( v -> {
//            CropImage.activity()
//                    .setGuidelines( CropImageView.Guidelines.ON)
//                   // .setActivityTitle("Image Crop")
//                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                  //  .setCropMenuCropButtonTitle("Done")
//                    .setRequestedSize(400, 400)
//                    //.setCropMenuCropButtonIcon(R.drawable.crop_image_menu_flip)
//                    .start(this);
//        } );
    }



    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                img.setImageURI(result.getUri());
//                Toast.makeText(
//                        this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
//                        .show();
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
//            }
//        }
    }
}


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);





        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri imageUris = CropImage.getPickImageResultUri(this, data);
            //    Uri resultUri = result.getUri();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUris);
                img.setImageBitmap( bitmap );
            } catch (IOException e) {
                e.printStackTrace();
            }


            // For API >= 23 we need to check specifically that we have permissions to read external
            // storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and
            // see if we get error.
          *//*  boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {

                mCurrentFragment.setImageUri(imageUri);
            }*//*
        }
    }*/



