package com.bignerdranch.criminalintent;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ksrchen on 12/14/13.
 */
public class CrimeLab {
    final  static  String FILENAME ="crimes.json";

    private static CrimeLab ourInstance;
    private Context mAppContext;
    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;

    private CrimeLab(Context appContext) {
        mAppContext = appContext.getApplicationContext();
        //mCrimes = new ArrayList<Crime>();
        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
        try{
        mCrimes = mSerializer.LoadCrimes();
        }catch (Exception exp){
            mCrimes = new ArrayList<Crime>();
            Log.e("CriminalIntent", "Error loading crimes", exp);
        }
    }
    public static CrimeLab getInstance(Context c) {
        if (ourInstance == null) ourInstance = new CrimeLab(c);
        return ourInstance;
    }
    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }
    public Crime getCrime(UUID id){
        for (Crime c: mCrimes){
            if (c.getId().equals(id)){
                return c;
            }
        }
        return null;
    }

    public  void addCrime(Crime c){
        mCrimes.add(c);
    }
    public  void deleteCrime(Crime c){
        mCrimes.remove(c);
    }
    public void saveCrimes() {
        try{
            mSerializer.SaveCrimes(mCrimes);
        }catch (Exception exp){
            Log.e("CriminalIntent", "Error in saveCrimes", exp);
        }
    }
}
