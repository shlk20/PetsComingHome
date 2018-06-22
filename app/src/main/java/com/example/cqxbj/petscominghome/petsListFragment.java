package com.example.cqxbj.petscominghome;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ThrowOnExtraProperties;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cqxbj on 22/05/18.
 */

public class petsListFragment extends Fragment implements OnCompleteListener<QuerySnapshot>{
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseDB;
    ListView petList;
    ArrayList<Pet> pets ;
    PetAdapter petAdapter;

    ProgressBar progressBar;
    FloatingActionButton refreshBtn;

    FusedLocationProviderClient client;
    Location mLocation;

    Double maxLaInRange;
    Double minLaInRange;
    Double maxLongInRange;
    Double minLongInRange;

    //-------------------------------search mode variables

    private Boolean searchSwitch=false;
    String searchName=null;
    String searchChip=null;
    String searchKind=null;
    String searchBreed=null;
    String searchSize=null;
    String searchLocation=null;
    Long searchDate=null;


    //-------------------------------MyPets mode variables
    private Boolean mypetsSwitch=false;


    //-------------------------------Context variables
    MainActivity activity;

    //-------------------------------Setting variables
    int searchRadius;
    Boolean lostSwitch;
    Boolean foundSwitch;

    //-------------------------------location
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.pets_list,container,false);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDB=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        locationRequest=new LocationRequest().setInterval(1000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallback=new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLocation=locationResult.getLastLocation();
                getSettings();
                setRange(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                client.removeLocationUpdates(this);
                getData();
            }
        };

        activity=(MainActivity) getActivity();
        petList =view.findViewById(R.id.petList);
        progressBar=view.findViewById(R.id.progressBarOnList);
        refreshBtn=view.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchSwitch)
                {
                    getData();
                }
                else
                    {
                    getDefault();
                }
            }
        });

        getDefault();
        return view;
    }

    public void getDefault()
    {

        UIforLoading();
        if(locationServiceEnabled())
        {
            client= LocationServices.getFusedLocationProviderClient(getContext());
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {

                    client.requestLocationUpdates(this.locationRequest,this.locationCallback,null);
                }
                else {
                    UIforComplete();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100 );
                }
            }
            else {
                client.requestLocationUpdates(this.locationRequest,this.locationCallback,null);
            }
        }
        else
        {
            pets=new ArrayList<>();
            petAdapter = new PetAdapter(activity,R.layout.list_item, pets);
            petList.setAdapter(petAdapter);
            Toast.makeText(activity,"Please enable the location service",Toast.LENGTH_LONG).show();
            UIforComplete();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==100)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getDefault();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void search(String searchName,String searchBreed,String searchSize,String searchChip,String searchKind,String searchLocation,Long searchDate)
    {
        this.searchName=searchName;
        this.searchBreed=searchBreed;
        this.searchSize=searchSize;
        this.searchChip=searchChip;
        this.searchKind=searchKind;
        this.searchDate=searchDate;
        this.searchLocation=searchLocation;
        this.searchSwitch=true;
        UIforLoading();
        getSettings();
        getData();
    }
    public void resetMode()
    {
         this.mypetsSwitch=false;
         this.searchSwitch=false;
         this.searchDate=null;
    }

    //----set UI for loading
    private void UIforLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
        refreshBtn.setVisibility(View.INVISIBLE);
        petList.setVisibility(View.INVISIBLE);
    }

    //--set UI for complete
    private void UIforComplete()
    {
        if(mypetsSwitch)
        {
            refreshBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            refreshBtn.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
        petList.setVisibility(View.VISIBLE);
    }

    private boolean locationServiceEnabled()
    {
        LocationManager lm=(LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)||lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            return  true;
        }else {
            return false;
        }
    }

    private void getSettings()
    {
        searchRadius=activity.sp.getInt("Radius",1);
        lostSwitch=activity.sp.getBoolean("LostBool",true);
        foundSwitch=activity.sp.getBoolean("FoundBool",true);
    }

    private  void setRange(LatLng latLng)
    {

        maxLaInRange=latLng.latitude+searchRadius/111d;
        minLaInRange=latLng.latitude-searchRadius/111d;
        maxLongInRange=latLng.longitude+searchRadius/(111*Math.cos(latLng.latitude*(Math.PI/180)));
        minLongInRange=latLng.longitude-searchRadius/(111*Math.cos(latLng.latitude*(Math.PI/180)));
    }

    private void getData()
    {

        Query petsQuery=firebaseDB.collection("Pet");
        if(searchSwitch)
        {
                if(!searchName.equals(""))
                {
                    petsQuery=petsQuery.whereEqualTo("Name", searchName);
                }
                if(!searchChip.equals(""))
                {
                    petsQuery=petsQuery.whereEqualTo("MicrochipNumber",searchChip);
                }
                if(!searchKind.equals(""))
                {
                    petsQuery=petsQuery.whereEqualTo("Kind",searchKind);
                }
                if (!searchBreed.equals(""))
                {
                    petsQuery=petsQuery.whereEqualTo("Breed",searchBreed);
                }
                if(!searchSize.equals(""))
                {
                    petsQuery=petsQuery.whereEqualTo("Size",searchSize);
                }
                if(!searchLocation.equals(""))
                {
                    petsQuery=petsQuery.whereEqualTo("Region",searchLocation);
                }
                if(searchDate!=0L)
                {
                    petsQuery=petsQuery.whereEqualTo("MissingSince",searchDate);
                }
                petsQuery.get().addOnCompleteListener(this);
        }
        else
        {
                firebaseDB.collection("Pet").whereGreaterThan("Latitude",minLaInRange)
                        .whereLessThan("Latitude",maxLaInRange)
                        .get().addOnCompleteListener(this);
        }

    }

    public void getMyPets()
    {
        UIforLoading();
        getSettings();
        mypetsSwitch=true;
        if(firebaseAuth.getCurrentUser()!=null)
        {
            firebaseDB.collection("Pet").whereEqualTo("Uid",firebaseAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(this);

        }
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        pets=new ArrayList<>();
        petAdapter = new PetAdapter(activity,R.layout.list_item, pets);
        petList.setAdapter(petAdapter);
        if(task.isSuccessful())
        {
            for(QueryDocumentSnapshot eachPetData:task.getResult())
            {

                Map eachPet=eachPetData.getData();
                final Pet p=new Pet(
                        eachPet.get("PetId").toString(),
                        eachPet.get("Uid").toString(),
                        eachPet.get("Name").toString(),
                        eachPet.get("Breed").toString(),
                        eachPet.get("Kind").toString(),
                        ((Number)eachPet.get("Age")).doubleValue(),
                        eachPet.get("Gender").toString(),
                        eachPet.get("Desexed").toString(),
                        eachPet.get("MicrochipNumber").toString(),
                        ((Number)eachPet.get("MissingSince")).longValue(),
                        ((Number) eachPet.get("Latitude")).doubleValue(),
                        ((Number) eachPet.get("Longitude")).doubleValue(),
                        eachPet.get("Status").toString(),
                        eachPet.get("Size").toString(),
                        eachPet.get("Color").toString(),
                        eachPet.get("Region").toString(),
                        eachPet.get("Description").toString(),
                        eachPet.get("Photo").toString()
                );

                if(searchSwitch)
                {
                        if (lostSwitch && foundSwitch)
                        {
                            addEachPet(p);
                        }
                        else if (lostSwitch && (!foundSwitch))
                        {
                            if (p.getStatus().equals("Lost")) {
                                addEachPet(p);
                            }
                        }
                        else if (foundSwitch && (!lostSwitch))
                        {
                            if (p.getStatus().equals("Found")) {
                                addEachPet(p);
                            }
                        }
                }
                else if(mypetsSwitch)
                {
                    if (lostSwitch && foundSwitch)
                    {
                        addEachPet(p);
                    }
                    else if (lostSwitch && (!foundSwitch))
                    {
                        if (p.getStatus().equals("Lost")) {
                            addEachPet(p);
                        }
                    }
                    else if (foundSwitch && (!lostSwitch))
                    {
                        if (p.getStatus().equals("Found")) {
                            addEachPet(p);
                        }
                    }
                }
                else
                {
                    if(p.getLng()<=maxLongInRange&&p.getLng()>=minLongInRange)
                    {

                        if(lostSwitch&&foundSwitch) {
                            addEachPet(p);
                        }
                        else if(lostSwitch&&(!foundSwitch))
                        {
                            if(p.getStatus().equals("Lost"))
                            {
                                addEachPet(p);
                            }
                        }
                        else if (foundSwitch&&(!lostSwitch))
                        {
                            if(p.getStatus().equals("Found"))
                            {
                                addEachPet(p);
                            }
                        }
                    }
                }
            }
            UIforComplete();
        }
        else
        {
            Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void addEachPet(final Pet p)
    {
        pets.add(p);
        firebaseStorage.getReference()
                .child(p.getPhotoUrl()).getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        p.bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        petAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //PetAdapter class
    class PetAdapter extends ArrayAdapter<Pet> {
        int rescourceid;
        Context context;
        public PetAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            rescourceid = resource;
            this.context=context;
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            final Pet pet = getItem(position);
            View view;
            if(convertView==null)
            {
                view = LayoutInflater.from(context).inflate(rescourceid, parent, false);
            }
            else
            {
                view=convertView;
            }

            view.setClickable(false);

            TextView name = (TextView) view.findViewById(R.id.NameTxt);
            name.setText(pet.getName());

            TextView kind= (TextView) view.findViewById(R.id.KindTxt);
            kind.setText(pet.getKind());

            TextView breed=(TextView) view.findViewById(R.id.BreedTxt);
            breed.setText(pet.getBreed());

            TextView color=(TextView) view.findViewById(R.id.ColorTxt);
            color.setText(pet.getColor());

            TextView location=(TextView)view.findViewById(R.id.RegionTxt);
            location.setText(pet.getRegion());

            TextView status=(TextView)view.findViewById(R.id.StatusTxt);
            status.setText(pet.getStatus());

            ImageView image = (ImageView) view.findViewById(R.id.imagePet);
            image.setImageBitmap(pet.bitmap);

            TextView goToDetails = (TextView) view.findViewById(R.id.Details);
            goToDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailsIntent=new Intent(getContext(),EachPetActivity.class);
                    try {
                        Pet intentPet=(Pet)pet.clone();
                        intentPet.bitmap=null;
                        detailsIntent.putExtra("thisPet",intentPet);
                        startActivity(detailsIntent);

                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            });

            return view;
        }

    }
}
