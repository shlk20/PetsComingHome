package com.example.cqxbj.petscominghome;

import android.icu.util.DateInterval;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by cqxbj on 23/05/18.
 */

public class Pet {

    private String mId;
    private String mUid;
    private String mName;
    private String mBreed;
    private String mKind;
    private double mAge;
    private String mGender;
    private String mDesexed;
    private String mMicrochip;
    private Calendar mDate;
    private double mLat;
    private double mLng;
    private String mStatus;
    private String mSize;
    private String mColor;
    private String mRegion;
    private String mDescription;
    private String mPhotoUrl;




    public Pet() {}  // Needed for Firebase

        public Pet(
                String id,
                String uid,
                String name,
                String breed,
                String kind,
                double age,
                String gender,
                String desexed,
                String microchip,
                Calendar date,
                double lat,
                double lng,
                String status,
                String size,
                String color,
                String region,
                String description,
                String photoUrl)
        {
            mId = id;
            mUid = uid;
            mName = name;
            mBreed = breed;
            mKind = kind;
            mAge = age;
            mGender = gender;
            mDesexed = desexed;
            mMicrochip = microchip;
            mDate = date;
            mLat = lat;
            mLng = lng;
            mStatus = status;
            mColor = color;
            mSize = size;
            mRegion = region;
            mDescription = description;
            mPhotoUrl = photoUrl;

        }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }

    public String getId() { return mId; }

    public void setId(String id) { mId = id; }

    public String getUid() { return mUid; }

    public void setUid(String uid) { mUid = uid; }

    public String getBreed() { return mBreed; }

    public void setBreed(String breed) { mBreed = breed; }

    public String getKind() { return mKind; }

    public void setKind(String kind) { mKind = kind; }

    public double getAge() { return mAge; }

    public void setAge(double age) { mAge = age; }

    public String getGender() { return mGender; }

    public void setGender(String gender) { mGender = gender; }

    public String getDesexed() { return mDesexed; }

    public void setDesexed(String desexed) { mDesexed = desexed; }

    public String getMicrochip() { return mMicrochip; }

    public void setMicrochip(String microchip) { mMicrochip = microchip; }

    public Calendar getDate() { return mDate; }

    public void setDate(Calendar date) { mDate = date; }

    public double getLat() { return mLat; }

    public void setLat(double lat) { mLat = lat; }

    public double getLng() { return mLng; }

    public void setLng(double lng) { mLng = lng; }

    public String getStatus() { return mStatus; }

    public void setStatus(String status) { mStatus = status; }

    public String getDescription() { return mDescription; }

    public void setDescription(String description) { mDescription = description; }

    public String getSize() { return mSize; }

    public void setSize(String size) { mSize = size; }

    public String getColor() { return mColor; }

    public void setColor(String color) { mColor = color; }

    public String getRegion() { return mRegion; }

    public void setRegion(String region) { mRegion = region; }

    public String getPhotoUrl() { return mPhotoUrl; }

    public void setPhotoUrl(String photoUrl) { mPhotoUrl = photoUrl; }

    public Pet(String name,int url)
    {
        this.name=name;
        this.ImageUrl=url;
    }
    String name;
    int ImageUrl;
}
