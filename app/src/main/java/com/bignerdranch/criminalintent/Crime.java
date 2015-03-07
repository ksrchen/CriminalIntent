package com.bignerdranch.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ksrchen
 * Date: 12/7/13
 * Time: 8:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto;
    private String mSuspect;

    final static String JSON_ID=  "id";
    final static String JSON_TTILE=  "title";
    final static String JSON_DATE=  "date";
    final static String JSON_SOLVED=  "solved";
    final static String JSON_PHOTO=  "photo";
    final static String JSON_SUSPECT=  "suspect";

    public Crime(){
        mId = UUID.randomUUID();
        mDate = new Date();
    }
    public Crime(JSONObject json) throws  JSONException{
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TTILE);
        mDate = new Date(json.getLong(JSON_DATE));
        mSolved = json.getBoolean(JSON_SOLVED);
        if (json.has(JSON_PHOTO)){
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        }
        if (json.has(JSON_SUSPECT)){
            setSuspect(json.getString(JSON_SUSPECT));
        }
    }
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_DATE, mDate.getTime());
        json.put(JSON_ID, mId);
        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_TTILE, mTitle);
        if (mPhoto != null){
            json.put(JSON_PHOTO, mPhoto.toJSON());
        }
        json.put(JSON_SUSPECT, getSuspect());
        return  json;

    }
    public UUID getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    @Override
    public String toString() {
        return mTitle;
    }


    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
}
