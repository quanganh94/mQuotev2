package com.example.image.mquotev2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.image.mquotev2.zoom.ImageZoomView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mac on 3/9/15.
 */
public class CameraProcess {
    Context context;
    RelativeLayout layout;
    CameraPreview mPreview;
    ImageZoomView Image;

    Camera mCamera;
    CameraProcess(RelativeLayout layout, Context ct, ImageZoomView Image){
        this.context=ct;
        this.layout=layout;
        this.Image=Image;
        this.mCamera = getCameraInstance();
    }

    public void start(){
        this.mCamera = getCameraInstance();
        if(mCamera!=null) {
            mPreview = new CameraPreview(context, mCamera);
            Image.setVisibility(View.GONE);
            layout.addView(mPreview);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mCamera.autoFocus(myAutoFocusCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public Bitmap take_picture(){
        mCamera.takePicture(null, null, mPicture);
        layout.removeView(mPreview);
        Image.setVisibility(View.VISIBLE);
        return picture;
    }
    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
        }
    };
    File pictureFile;
    public Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            pictureFile = getOutputMediaFile(1);
            if (pictureFile == null) {
                Toast.makeText(context,"Error creating media file, check storage permissions",Toast.LENGTH_LONG).show();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                takePicture();
                fos.close();
            } catch (FileNotFoundException e) {
//                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
//                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };
    Bitmap picture;
    public void takePicture() {
        picture = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
    }
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/mQuote_temp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("mQuote_temp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath(), "camera.jpg");
        } else {
            return null;
        }
        return mediaFile;
    }
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
