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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cqxbj on 22/05/18.
 */

public class AddNewPetFragment extends Fragment implements View.OnClickListener,DatePickerDialog.OnDateSetListener, OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseStorage mStorage;
    StorageReference mStorageReference;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private ImageView imageView;

    View mView;


    TextView mNameTxt;
    TextView mAgeTxt;
    TextView mDescriptionTxt;
    TextView mColorTxt;
    TextView mMicrochipTxt;
    TextView mBreedTxt;

    LatLng mLatLng;

    Spinner spinnerForStatus;
    Spinner spinnerForType;
    Spinner spinnerForGender;
    Spinner spinnerForDesex;
    Spinner spinnerForSize;

    ArrayAdapter<String> statusAdapter;
    ArrayAdapter<String> typeAdapter;
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> desexAdaper;
    ArrayAdapter<String> sizeAdaper;

    String[] statusData={"Lost","Found","Choose status"};
    String[] typeData={"Dog","Cat","Pig","Rabbit","Choose pet type"};
    String[] genderData={"Male","Female","Choose pet gender"};
    String[] deData={"Yes","No","Is your pet desexed?"};
    String[] sizeData={"Small","Medium", "Large","What is the pet size?"};

    TextView ChooseDate;
    int day,year,month;
    DatePickerDialog datePickerDialog;

    Button uploadPhotoButton;
    String mPhotoPath;

    GoogleMap mGoogleMap;
    MapView mMapView;


    Button addPet;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //datepicker Dialog for choosing date
        Calendar c=Calendar.getInstance();
        this.year=c.get(Calendar.YEAR);
        this.month=c.get(Calendar.MONTH);
        this.day=c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog=new DatePickerDialog(getContext(),this,year,month,day);

        statusAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,statusData)
        {
            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,typeData)
        {
            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,genderData)
        {
            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        desexAdaper=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,deData)
        {
            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };
        desexAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sizeAdaper=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,sizeData)
        {
            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };
        sizeAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        myRef = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         mView = inflater.inflate(R.layout.fragment_add_new_pet,container,false);

        spinnerForStatus = mView.findViewById(R.id.spinnerForStatus);
        spinnerForStatus.setAdapter(statusAdapter);
        spinnerForStatus.setSelection(statusData.length-1);

        spinnerForType = mView.findViewById(R.id.spinnerForType);
        spinnerForType.setAdapter(typeAdapter);
        spinnerForType.setSelection(typeData.length-1);

        spinnerForGender = mView.findViewById(R.id.spinnerForGender);
        spinnerForGender.setAdapter(genderAdapter);
        spinnerForGender.setSelection(genderData.length-1);

        spinnerForDesex = mView.findViewById(R.id.spinnerForDe);
        spinnerForDesex.setAdapter(desexAdaper);
        spinnerForDesex.setSelection(deData.length-1);

        spinnerForSize = mView.findViewById(R.id.spinnerForSize);
        spinnerForSize.setAdapter(sizeAdaper);
        spinnerForSize.setSelection(sizeData.length-1);

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

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView =  mView.findViewById(R.id.map);
        if (mMapView != null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

        }
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
                addPet();
                break;

        }
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ChooseDate.setText(month+"/"+dayOfMonth+"/"+year);
    }

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
            filePath = data.getData();
            try {
                Context applicationContext = MainActivity.getContextOfApplication();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addPet()
    {
        uploadImage();


        String mId = myRef.push().getKey();
        String mUid = user.getUid();
        String mName = mNameTxt.getText().toString();
        String mBreed = mBreedTxt.getText().toString();
        String mKind = spinnerForType.getSelectedItem().toString();
        double mAge = Double.parseDouble(mAgeTxt.getText().toString());
        String mGender = spinnerForGender.getSelectedItem().toString();
        String mDesexed = spinnerForDesex.getSelectedItem().toString();
        String mMicrochip = mMicrochipTxt.getText().toString();
        Calendar mDate = new GregorianCalendar(year, month, day);
        double mLat = mLatLng.latitude;
        double mLng = mLatLng.longitude;
        String mStatus = spinnerForStatus.getSelectedItem().toString();
        String mSize = spinnerForSize.getSelectedItem().toString();
        String mColor = mColorTxt.getText().toString();
        String mRegion = getLocationName(mLatLng);
        String mDescription = mDescriptionTxt.getText().toString();
        String mPhotoUrl = mPhotoPath;

        Pet pet = new Pet(
                mId,
                mUid,
                mName,
                mBreed,
                mKind,
                mAge,
                mGender,
                mDesexed,
                mMicrochip,
                mDate,
                mLat,
                mLng,
                mStatus,
                mSize,
                mColor,
                mRegion,
                mDescription,
                mPhotoUrl);

        myRef.child("Pet").child(mId).setValue(pet);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Getting location accessed
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Trying to get access
                checkLocationPermission();
            }
        }
        else {
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                mGoogleMap.clear();
                mGoogleMap.addMarker(new MarkerOptions().position(point));

                mLatLng = point;
            }
        });

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("The app only will work if you allow to locate yourself")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    public String getLocationName (LatLng latLng)
    {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String cityName = addresses.get(0).getAddressLine(0);
            return cityName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading data...");
            progressDialog.show();

            mPhotoPath = "images/"+ UUID.randomUUID().toString();
            StorageReference ref = mStorageReference.child(mPhotoPath);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Pet was successfully added", Toast.LENGTH_LONG).show();
                            getActivity().getFragmentManager().popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
