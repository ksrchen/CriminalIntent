package com.bignerdranch.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by ksrchen on 12/29/13.
 */
public class ImageFragment extends DialogFragment {
    public static final String EXTRA_PHOTO  = "com.bignerdrand.criminalintent.photo";
    private ImageView mImageView;

    public static  ImageFragment getInstance(Photo photo){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PHOTO, photo);

        ImageFragment imageFragment = new ImageFragment();
        imageFragment.setArguments(args);
        imageFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        return  imageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        Photo photo = (Photo)getArguments().getSerializable(EXTRA_PHOTO);
        String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
        BitmapDrawable bitmapDrawable = PictureUtils.getScaledDrawable(getActivity(), path);
        mImageView.setImageDrawable(bitmapDrawable);
        mImageView.setRotation(PictureUtils.getDisplayOrientation(getActivity(), photo.getOrientation()));

        return  mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
