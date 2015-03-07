package com.bignerdranch.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by ksrchen on 12/28/13.
 */
public class CrimeCameraFragment extends Fragment {
    private static  final String TAG = "CrimeCameraFragment";
    public static  final String EXTRA_PHOTO_FILENAME = "com.bignerdranch.criminalintent.photo_filename";
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private  Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream os = null;
            boolean success =true;
            try{
                os = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                os.write(data);
            }   catch (IOException exp){
                Log.e(TAG, "Error writing to file", exp);
                success = false;
            } finally {
                if (os != null){
                    try{
                    os.close();
                    }catch (IOException exp){
                        Log.e(TAG, "Error closing file", exp);
                        success = false;
                    }
                }
            }
            if (success){
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, fileName);
                getActivity().setResult(Activity.RESULT_OK, i);

            }
            else{
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);
        Button takePicture = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null){
                    mCamera.takePicture(mShutterCallback, null, mPictureCallback);
                }
            }
        });

        mSurfaceView =(SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback( new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if (mCamera != null){
                        mCamera.setDisplayOrientation(PictureUtils.getDisplayOrientation(getActivity(),
                                PictureUtils.getCameraOrientation(0)));
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exp){
                    Log.e(TAG, "Error in setting preview  display", exp);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null) return;
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try{
                    mCamera.startPreview();
                } catch (Exception exp){
                    Log.e(TAG, "Could not start previw", exp);
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null){
                    mCamera.stopPreview();
                }

            }
        });

        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);
        return v;
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> supportedPreviewSizes, int width, int height) {
        Camera.Size bestSize = supportedPreviewSizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s: supportedPreviewSizes){
            int area = s.width * s.height;
            if (area > largestArea){
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    @Override
    @TargetApi(9)
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            mCamera = Camera.open(0);
        } else{
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }
}