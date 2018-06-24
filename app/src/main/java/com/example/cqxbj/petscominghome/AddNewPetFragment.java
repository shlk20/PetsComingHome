package com.example.cqxbj.petscominghome;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cqxbj on 22/05/18.
 */

public class AddNewPetFragment extends Fragment implements  View.OnClickListener, DatePickerDialog.OnDateSetListener, OnMapReadyCallback{

    //---------Firebase
    FirebaseFirestore mFirestore;
    FirebaseStorage mStorage;
    StorageReference mStorageReference;

    //-----------Some UI widget
    ImageView imageView;
    View mView;
    TextView mNameTxt;
    TextView mAgeTxt;
    TextView mDescriptionTxt;
    TextView mColorTxt;
    TextView mMicrochipTxt;
    TextView mBreedTxt;


    //Spinners
    Spinner spinnerForStatus;
    Spinner spinnerForType;
    Spinner spinnerForGender;
    Spinner spinnerForDesex;
    Spinner spinnerForSize;

    //ArrayAdapter
    ArrayAdapter<String> statusAdapter;
    ArrayAdapter<String> typeAdapter;
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> desexAdapter;
    ArrayAdapter<String> sizeAdapter;

    //Some strings for the adapters
    String[] statusData={"* Choose status","Lost","Found"};
    String[] typeData={"* Choose pet type","Dog","Cat","Bird","Pig","Reptile","Rodent","Others"};
    String[] genderData={"* Choose pet gender","Male","Female"};
    String[] deData={" Is your pet desexed?","Yes","No",};
    String[] sizeData={"* What is the pet size?","Small","Medium", "Large"};

    //spinner value
    String mKind="";
    String mGender="";
    String mDesexed="";
    String mSize="";
    String mStatus="";


    //The upload date part
    Button ChooseDate;
    Long mDateVaule;
    DatePickerDialog datePickerDialog;

    //The upload photo part
    Button uploadPhotoButton;
    String mPhotoPath;
    final int PICK_IMAGE_REQUEST = 10;
    byte[] uploadImageData;


    //--------Google map
    GoogleMap mGoogleMap;
    MapView mMapView;
    Button displayMapButton;
    Button hideMapButton;
    //------------Latlng for the new added pet
    LatLng mLatLng;

    //--------Add a new pet button
    Button addPet;

    //Activity
    MainActivity activity;

    //-----------------------------------initialization
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_new_pet,container,false);
        activity=(MainActivity)getActivity();
        Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog=new DatePickerDialog(activity,this,year,month,day);

        statusAdapter=new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,statusData);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeAdapter=new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,typeData);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderAdapter=new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,genderData);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        desexAdapter=new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,deData);
        desexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sizeAdapter=new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,sizeData);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mFirestore=FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        spinnerForStatus = mView.findViewById(R.id.spinnerForStatus);
        spinnerForStatus.setAdapter(statusAdapter);
        spinnerForStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    mStatus="";
                }
                else
                {
                    mStatus=spinnerForStatus.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerForStatus.setSelection(0);

        spinnerForType = mView.findViewById(R.id.spinnerForType);
        spinnerForType.setAdapter(typeAdapter);
        spinnerForType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    mKind="";
                }
                else
                {
                    mKind=spinnerForType.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerForType.setSelection(0);

        spinnerForGender = mView.findViewById(R.id.spinnerForGender);
        spinnerForGender.setAdapter(genderAdapter);
        spinnerForGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    mGender="";
                }
                else
                {
                    mGender=spinnerForGender.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerForGender.setSelection(0);

        spinnerForDesex = mView.findViewById(R.id.spinnerForDe);
        spinnerForDesex.setAdapter(desexAdapter);
        spinnerForDesex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    mDesexed="";
                }
                else
                {
                    mDesexed=spinnerForDesex.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerForDesex.setSelection(0);

        spinnerForSize = mView.findViewById(R.id.spinnerForSize);
        spinnerForSize.setAdapter(sizeAdapter);
        spinnerForSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    mSize="";
                }
                else
                {
                    mSize=spinnerForSize.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerForSize.setSelection(0);


        mNameTxt = mView.findViewById(R.id.inputName);
        mBreedTxt = mView.findViewById(R.id.inputBreed);
        mAgeTxt = mView.findViewById(R.id.inputAge);
        mDescriptionTxt = mView.findViewById(R.id.addInfo);
        mMicrochipTxt = mView.findViewById(R.id.inputMicrochipNb);
        mColorTxt = mView.findViewById(R.id.inputColor);

        ChooseDate = mView.findViewById(R.id.ChooseDate);
        ChooseDate.setOnClickListener(this);

        uploadPhotoButton = mView.findViewById(R.id.uploadPhotoBtn);
        uploadPhotoButton.setOnClickListener(this);

        imageView = mView.findViewById(R.id.imgView);

        addPet = mView.findViewById(R.id.addPetBtn);
        addPet.setOnClickListener(this);


        displayMapButton=mView.findViewById(R.id.mapBtn);
        displayMapButton.setOnClickListener(this);

        hideMapButton=mView.findViewById(R.id.hideMapBtn);
        hideMapButton.setOnClickListener(this);
        hideMapButton.setVisibility(View.INVISIBLE);

        mMapView =  mView.findViewById(R.id.map);
        mMapView.setVisibility(View.INVISIBLE);
        if (mMapView != null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        return mView;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.ChooseDate:
                datePickerDialog.show();
                break;
            case R.id.uploadPhotoBtn:
                if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectPhoto();
                }
                else
                {
                    getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                }
                break;
            case R.id.addPetBtn:
                if(mDateVaule==null||uploadImageData==null||mLatLng==null||mStatus.equals("")||mKind.equals("")||mGender.equals("")||mSize.equals(""))
                {
                    Toast.makeText(activity,"Please fill in the required information",Toast.LENGTH_SHORT).show();
                }
                else {
                    addPet();
                }
                activity.hideTheInput();
                break;
            case R.id.mapBtn:
                mMapView.setVisibility(View.VISIBLE);
                hideMapButton.setVisibility(View.VISIBLE);
                break;
            case R.id.hideMapBtn:
                mMapView.setVisibility(View.INVISIBLE);
                hideMapButton.setVisibility(View.INVISIBLE);
                break;

        }
    }

    //------------Select the vaule of date
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ChooseDate.setText(dayOfMonth+"/"+month+"/"+year);
        mDateVaule=new GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
    }

    //------------Go to select a photo
    public void selectPhoto()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select photo of the pet"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            //Get the uploadImageDate and display the image
            try {
                Uri filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), filePath);
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,30,out);
                uploadImageData=out.toByteArray();
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    //--------------------Add a new pet
    public void addPet()
    {
        //-----------------Upload the image
        uploadImage();

        String mPetId=  UUID.randomUUID().toString();
        String mUid =FirebaseAuth.getInstance().getCurrentUser().getUid();
        String mName = mNameTxt.getText().toString();
        String mBreed = mBreedTxt.getText().toString();

        double mAge=0;
        if(mAgeTxt.getText().length()!=0)
        {mAge = Double.parseDouble(mAgeTxt.getText().toString());}

        String mMicrochip = mMicrochipTxt.getText().toString();
        Long mDate =mDateVaule;
        double mLat = mLatLng.latitude;
        double mLng = mLatLng.longitude;
        String mColor = mColorTxt.getText().toString();
        String mRegion = getLocationName(mLatLng);
        String mDescription = mDescriptionTxt.getText().toString();
        String mPhotoUrl = mPhotoPath;

        Map<String,Object> petMap=new HashMap<String,Object>();
        petMap.put("Uid",mUid);
        petMap.put("PetId",mPetId);
        petMap.put("Name",mName);
        petMap.put("Breed",mBreed);
        petMap.put("Kind",mKind);
        petMap.put("Age",mAge);
        petMap.put("Gender",mGender);
        petMap.put("Desexed",mDesexed);
        petMap.put("MicrochipNumber",mMicrochip);
        petMap.put("MissingSince",mDate);
        petMap.put("Latitude",mLat);
        petMap.put("Longitude",mLng);
        petMap.put("Status",mStatus);
        petMap.put("Size",mSize);
        petMap.put("Color",mColor);
        petMap.put("Region",mRegion);
        petMap.put("Description",mDescription);
        petMap.put("Photo",mPhotoUrl);
        mFirestore.collection("Pet").document(mPetId).set(petMap);
    }

    //-----------------Upload the image
    private void uploadImage() {

        if(uploadImageData != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading data...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            mPhotoPath = "images/"+ UUID.randomUUID().toString();
            StorageReference ref = mStorageReference.child(mPhotoPath);

            ref.putBytes(uploadImageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Pet was successfully added", Toast.LENGTH_LONG).show();
                            activity.hideTheFragment(activity.addNewPetFragment);
                            activity.showTheFragment(activity.petsListFragment);
                            activity.petsListFragment.getDefault();
                            activity.getSupportActionBar().setTitle("Pets around you");
                            resetPage();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = 100.0*(((double)taskSnapshot.getBytesTransferred())/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    //------------------Google map initialization
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(activity);

        mGoogleMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else {
            mGoogleMap.setMyLocationEnabled(true);
        }

        //------------------Assign the mLatlng (the Latlng of this new pet)
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                mGoogleMap.clear();
                mGoogleMap.addMarker(new MarkerOptions().position(point));
                mLatLng = point;

            }
        });

    }

    //----------------------get the location name
    public String getLocationName (LatLng latLng)
    {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String city = addresses.get(0).getLocality();
            return  city;

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


    public void resetPage()
    {
        mNameTxt.setText("");
        mAgeTxt.setText("");
        mDescriptionTxt.setText("");
        mColorTxt.setText("");
        mMicrochipTxt.setText("");
        mBreedTxt.setText("");
        spinnerForDesex.setSelection(0);
        spinnerForGender.setSelection(0);
        spinnerForSize.setSelection(0);
        spinnerForStatus.setSelection(0);
        spinnerForType.setSelection(0);
        mKind="";
        mGender="";
        mDesexed="";
        mSize="";
        mStatus="";
        mLatLng=null;
        mGoogleMap.clear();
        mPhotoPath=null;
        imageView.setImageResource(R.drawable.paw);
        uploadImageData=null;
        ChooseDate.setText("* Choose a date");
        mDateVaule=null;
        if(mMapView.getVisibility()==View.VISIBLE)
        {
            mMapView.setVisibility(View.INVISIBLE);
            hideMapButton.setVisibility(View.INVISIBLE);
        }
    }

    //--------------------Reset when the fragment is hidden
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden)
        {
            resetPage();
        }
    }
}
