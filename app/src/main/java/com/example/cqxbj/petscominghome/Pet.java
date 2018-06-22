package com.example.cqxbj.petscominghome;

import android.graphics.Bitmap;
import android.icu.util.DateInterval;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cqxbj on 23/05/18.
 */

public class Pet implements Serializable,Cloneable {

        private String mId;
        private String mUid;
        private String mName;
        private String mBreed;
        private String mKind;
        private Double mAge;
        private String mGender;
        private String mDesexed;
        private String mMicrochip;
        private Long mDate;
        private Double mLat;
        private Double mLng;
        private String mStatus;
        private String mSize;
        private String mColor;
        private String mRegion;
        private String mDescription;
        private String mPhotoUrl;


        public Bitmap bitmap;

        public Pet(
                String PetId,
                String Uid,
                String Name,
                String Breed,
                String Kind,
                Double Age,
                String Gender,
                String Desexed,
                String MicrochipNumber,
                Long MissingSince,
                Double Latitude,
                Double Longitude,
                String Status,
                String Size,
                String Color,
                String Region,
                String Description,
                String Photo)
        {
                mId = PetId;
                mUid = Uid;
                mName = Name;
                mBreed = Breed;
                mKind = Kind;
                mAge = Age;
                mGender = Gender;
                mDesexed = Desexed;
                mMicrochip = MicrochipNumber;
                mDate = MissingSince;
                mLat = Latitude;
                mLng = Longitude;
                mStatus = Status;
                mColor = Color;
                mSize = Size;
                mRegion = Region;
                mDescription = Description;
                mPhotoUrl = Photo;

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

        public Double getAge() { return mAge; }

        public void setAge(double age) { mAge = age; }

        public String getGender() { return mGender; }

        public void setGender(String gender) { mGender = gender; }

        public String getDesexed() { return mDesexed; }

        public void setDesexed(String desexed) { mDesexed = desexed; }

        public String getMicrochip() { return mMicrochip; }

        public void setMicrochip(String microchip) { mMicrochip = microchip; }

        public Long getDate() { return mDate; }

        public void setDate(Long date) { mDate = date; }

        public Double getLat() { return mLat; }

        public void setLat(double lat) { mLat = lat; }

        public Double getLng() { return mLng; }

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

        @Override
        protected Object clone() throws CloneNotSupportedException {
                Pet pet =(Pet)super.clone();
                return pet;
        }
}
