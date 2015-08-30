package com.example.image.mquotev2;

import android.graphics.Bitmap;

/**
 * Created by ZoZy on 1/21/2015.
 */
public class Image {
    Bitmap bitmap,blurred;

    public int x, y;
    public int width(){
        if(bitmap==null) return 0;
        return bitmap.getWidth();
    }
    public int height(){
        if(bitmap==null) return 0;
        return bitmap.getHeight();
    }

    public int long_edge(){
        return Math.max(width(),height());
    }
}
