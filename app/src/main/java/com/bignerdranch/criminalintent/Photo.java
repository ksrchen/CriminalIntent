package com.bignerdranch.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ksrchen on 12/29/13.
 */
public class Photo implements Serializable {
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_ORIENTATION = "orientation";
    private String mFileName;
    private int mOrientation;

    public String getFileName() {
        return mFileName;
    }

    public Photo(String fileName, int orientation){
        mFileName = fileName;
        setOrientation(orientation);
    }
    public Photo(JSONObject json) throws JSONException{
        mFileName = json.getString(JSON_FILENAME);
        if (json.has(JSON_ORIENTATION)){
            setOrientation(json.getInt(JSON_ORIENTATION));
        }
    }
    public  JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_FILENAME, mFileName);
        jsonObject.put(JSON_ORIENTATION, getOrientation());
        return jsonObject;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }
}
