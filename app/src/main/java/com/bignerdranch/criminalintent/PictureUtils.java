package com.bignerdranch.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.widget.ImageView;

/**
 * Created by ksrchen on 12/29/13.
 */
public class PictureUtils {
    @SuppressWarnings("depreciation")
    public static BitmapDrawable getScaledDrawable(Activity activity, String fileName){
        Display display = activity.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        float srcWidth =  options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > destHeight){
                inSampleSize = Math.round(srcHeight / destHeight);
            }else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(fileName, options);
        return new BitmapDrawable(activity.getResources(), bitmap);

    }
    public static void cleanImageView (ImageView imageView){
        if (!(imageView.getDrawable() instanceof BitmapDrawable))
            return;

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        bitmapDrawable.getBitmap().recycle();
        imageView.setImageDrawable(null);

    }

    public static  int getCameraOrientation(int cameraId) {
        Camera.CameraInfo camerInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, camerInfo);
        return camerInfo.orientation;
    }
    
    public static int getDisplayOrientation(Activity activity, int imageOrientation) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result = (imageOrientation - degrees + 360) % 360;
        return result;
    }
}
