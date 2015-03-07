package com.bignerdranch.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by ksrchen on 12/14/13.
 */
public class CrimeListFragment extends ListFragment {
    private final static String TAG = "crimeintent";
    private ArrayList<Crime> mCrimes;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    public  interface Callbacks {
       public void onCrimeSelect(Crime crime);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.crime_title);
        mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();

        ArrayAdapter<Crime> adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
        setRetainInstance(true);
        mSubtitleVisible = false;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
        //Log.d(TAG, c.getTitle());
        //Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        //i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        //startActivity(i);
        mCallbacks.onCrimeSelect(c);
    }
    private  class  CrimeAdapter extends ArrayAdapter<Crime>{
           public CrimeAdapter(ArrayList<Crime> crimes){
               super(getActivity(), 0, crimes);
           }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
            }

            Crime c = getItem(position);

            ((TextView) convertView.findViewById(R.id.crime_list_titleTextview)).setText(c.getTitle());
            ((TextView) convertView.findViewById(R.id.crime_list_dateTextview)).setText(c.getDate().toString());
            ((CheckBox) convertView.findViewById(R.id.crime_list_solvedCheckbox)).setChecked(c.isSolved());
            return convertView;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem item = menu.findItem(R.id.memu_item_show_subtitle);
        if (mSubtitleVisible && item != null){
            item.setTitle(R.string.hide_subtitle);
        }
    }
    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.memu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);
                /*Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
                startActivityForResult(i, 0);
                return true;
                */
                ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
                mCallbacks.onCrimeSelect(crime);
                return  true;
            case R.id.memu_item_show_subtitle:
                if (getActivity().getActionBar().getSubtitle() == null){
                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
                    item.setTitle(R.string.hide_subtitle);
                    mSubtitleVisible = true;
                }
                else{
                    getActivity().getActionBar().setSubtitle(null);
                    item.setTitle(R.string.show_subtitle);
                    mSubtitleVisible = false;
                }
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View v =  super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_crime_list_view, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                mSubtitleVisible ){
            getActivity().getActionBar().setSubtitle(R.string.subtitle);
        }
        ListView list = (ListView)v.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            registerForContextMenu(list);
        }else{
            list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_item_delete:
                            CrimeAdapter crimeAdapter = (CrimeAdapter) getListAdapter();
                            CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
                            for (int i=crimeAdapter.getCount()-1; i>=0; i--){
                                if (getListView().isItemChecked(i)){
                                    crimeLab.deleteCrime(crimeAdapter.getItem(i));
                                }
                            }
                            mode.finish();
                            crimeAdapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete:
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                CrimeAdapter crimeAdapter = (CrimeAdapter) getListAdapter();
                Crime crime = crimeAdapter.getItem(menuInfo.position);
                CrimeLab.getInstance(getActivity()).deleteCrime(crime);
                crimeAdapter.notifyDataSetChanged();
                return  true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    public void updateUI() {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }
}