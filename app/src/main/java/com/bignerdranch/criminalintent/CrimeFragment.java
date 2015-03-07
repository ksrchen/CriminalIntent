package com.bignerdranch.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import javax.microedition.khronos.opengles.GL10Ext;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ksrchen
 * Date: 12/7/13
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrimeFragment extends Fragment {
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mSuspectButton;

    public final  static String EXTRA_CRIME_ID = "com.bignerdranch.criminalintent.extra_crimeId";
    private  final static String DIALOG_DATE= "DATE";
    private  final static String DIALOG_IMAGE= "IMAGE";
    private  final static String TAG= "CrimeFragment";

    private Callbacks mCallbacks;

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    public  interface Callbacks {
        public void onCrimeUpdated(Crime crime);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime((crimeId));
    }
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_crime, container,false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                NavUtils.getParentActivityName(getActivity()) != null ){
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mTitleField = (EditText)view.findViewById(R.id.crime_title);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                mCallbacks.onCrimeUpdated(mCrime);
                getActivity().setTitle(mCrime.getTitle());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //mCrime.setTitle(s.toString());
            }
        });

        mTitleField.setText(mCrime.getTitle());
        mDateButton = (Button)view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                mCallbacks.onCrimeUpdated(mCrime);
            }
        });
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mPhotoButton = (ImageButton)view.findViewById(R.id.crime_ImageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            mPhotoButton.setEnabled(false);
        }
        mPhotoView = (ImageView)view.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = mCrime.getPhoto();
                if (photo == null){
                    return;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ImageFragment.getInstance(photo).show(fm, DIALOG_IMAGE);
            }
        });

        Button reportButton = (Button)view.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton = (Button)view.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mCallbacks.onCrimeUpdated(mCrime);
            updateDate();
        }
        else if (requestCode == REQUEST_PHOTO){
            String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (fileName != null){
               // Log.i(TAG, "filename:" + fileName);
                mCrime.setPhoto(new Photo(fileName, PictureUtils.getCameraOrientation(0)));
                //Log.i(TAG, "Crime:" + mCrime.getTitle() + " has a photo");
                mCallbacks.onCrimeUpdated(mCrime);
                showPhoto();
            }
        }
        else if (requestCode == REQUEST_CONTACT){
            Uri contactUri = data.getData();
            String[] queryFields = new String [] {ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            if (c.getCount() <= 0){
                c.close();
                return;
            }
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mCallbacks.onCrimeUpdated(mCrime);
            mSuspectButton.setText(suspect);
            c.close();
        }
    }

    private void updateDate() {
        DateFormat dateFormat = new SimpleDateFormat("E MMM dd, yyyy");
        String dateString = dateFormat.format(mCrime.getDate());
        mDateButton.setText(dateString);
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    public static Fragment getInstance(UUID crimeId){
        Bundle arguments = new Bundle();
        arguments.putSerializable(EXTRA_CRIME_ID, crimeId);

        Fragment fragment = new CrimeFragment();
        fragment.setArguments(arguments);
        return  fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).saveCrimes();
    }

    private  void showPhoto(){
        Photo photo = mCrime.getPhoto();
        BitmapDrawable bitmapDrawable = null;
        if (photo != null){
            String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
            bitmapDrawable = PictureUtils.getScaledDrawable(getActivity(), path);
            mPhotoView.setRotation(PictureUtils.getDisplayOrientation(getActivity(), photo.getOrientation()));

        }
        mPhotoView.setImageDrawable(bitmapDrawable);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }
    private String getCrimeReport() {
        String solvedString = mCrime.isSolved()? getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        String dateString = android.text.format.DateFormat.format("EEE, MMM dd", mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        String suspectString = suspect==null?getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, suspect);

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspectString);
    }
}
