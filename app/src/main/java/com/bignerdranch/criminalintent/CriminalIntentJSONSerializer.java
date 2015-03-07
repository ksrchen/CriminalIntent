package com.bignerdranch.criminalintent;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by ksrchen on 12/26/13.
 */
public class CriminalIntentJSONSerializer {
    private  Context mContext;
    private String mFile;
    public CriminalIntentJSONSerializer(Context context, String file) {
        mContext = context;
        mFile = file;
    }

    public void SaveCrimes(ArrayList<Crime> crimes) throws IOException, JSONException {
        JSONArray array = new JSONArray();
        for (Crime c : crimes){
            array.put(c.toJson());
        }

        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFile, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        }
        finally {
            if (writer != null){
                writer.close();
            }
        }
    }
    public ArrayList<Crime> LoadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try{
            InputStream in = mContext.openFileInput(mFile);
            reader = new BufferedReader( new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                      jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for(int i=0; i<array.length();i++){
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        }   catch (Exception exp)
        {}
        finally {
            if (reader != null){
                reader.close();
            }
        }

        return crimes;
    }
}
