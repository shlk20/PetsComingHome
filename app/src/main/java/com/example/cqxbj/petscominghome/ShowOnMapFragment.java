package com.example.cqxbj.petscominghome;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.MapMaker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ThrowOnExtraProperties;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;

public class ShowOnMapFragment extends Fragment implements OnMapReadyCallback {

    //----------Firebase
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;

    //----------Refresh button and progress bar
    FloatingActionButton refreshBtn;
    ProgressBar pb;

    //----------Map
    GoogleMap map;
    MapView mapView;
    ArrayList<Marker> markers;

    //----------Pets
    ArrayList<Pet> pets;

    //----------Activity
    MainActivity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_show_on_map,container,false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        mapView=view.findViewById(R.id.showOnMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        pb=view.findViewById(R.id.progressBarOnMap);
        refreshBtn=view.findViewById(R.id.refreshMapBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPets();
            }
        });
        activity=(MainActivity) getActivity();
        return view;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-41,173),5));
        setLocationEnabled(true);
        getPets();

    }

    public void setLocationEnabled(Boolean bool)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(bool);
            }
        }
        else {
            map.setMyLocationEnabled(bool);
        }

    }

    //----------------------Get pets
    private void getPets()
    {
        markers=new ArrayList<Marker>();
        pets=new ArrayList<Pet>();
        pb.setVisibility(View.VISIBLE);
      firebaseFirestore.collection("Pet")
              .get()
              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    pb.setVisibility(View.INVISIBLE);
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
                        pets.add(p);

                    }
                    displayPets();
                }
                else
                {
                    Toast.makeText(activity,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //------------------------------Display these pets
    private void displayPets()
    {
        map.clear();
        for(Pet eachPet:pets)
        {
            BitmapDescriptor thisIcon;
            switch (eachPet.getKind())
            {
                case "Dog":
                    thisIcon=BitmapDescriptorFactory.fromResource(R.drawable.dogmapmarker);
                    break;
                case "Cat":
                    thisIcon=BitmapDescriptorFactory.fromResource(R.drawable.catmapmarker);
                    break;
                case "Pig":
                    thisIcon=BitmapDescriptorFactory.fromResource(R.drawable.pigmapmarker);
                    break;
                case "Bird":
                    thisIcon=BitmapDescriptorFactory.fromResource(R.drawable.birdmapmarker);
                    break;
                case "Rodent":
                    thisIcon=BitmapDescriptorFactory.fromResource(R.drawable.rodentmapmarker);
                    break;
                default:
                    thisIcon=BitmapDescriptorFactory.fromResource(R.drawable.othermarker);
                    break;
            }
            Marker thisMarker=map.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(eachPet.getLat(),eachPet.getLng()))
                            .icon(thisIcon)
                            .title(eachPet.getName())
            );

            markers.add(thisMarker);

        }


        //-----------------------------------The info window on this map
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            TextView name;
            TextView kind;
            TextView breed;
            TextView color;
            TextView status;
            View view;
            ImageView imageView;
            @Override
            public View getInfoWindow(Marker marker) {
                final Pet petOnThisMark=pets.get(markers.indexOf(marker));

                view=LayoutInflater.from(getContext()).inflate(R.layout.info_window_on_map,null);
                name=view.findViewById(R.id.nameInfoWindow);
                kind=view.findViewById(R.id.kindInfoWindow);
                breed=view.findViewById(R.id.breedInfoWindow);
                color=view.findViewById(R.id.colorInfoWindow);
                status=view.findViewById(R.id.statusInfoWindow);
                view.setBackgroundColor(Color.WHITE);

                if(petOnThisMark.getName().equals("")) name.setText("N/A");
                else name.setText(petOnThisMark.getName());

                if(petOnThisMark.getKind().equals("")) kind.setText("N/A");
                else kind.setText(petOnThisMark.getKind());

                if(petOnThisMark.getBreed().equals("")) breed.setText("N/A");
                else breed.setText(petOnThisMark.getBreed());

                if(petOnThisMark.getColor().equals("")) color.setText("N/A");
                else color.setText(petOnThisMark.getColor());

                if(petOnThisMark.getStatus().equals("")) status.setText("N/A");
                else status.setText(petOnThisMark.getStatus());

                imageView=view.findViewById(R.id.imageInfoWindow);
                imageView.setImageBitmap(petOnThisMark.bitmap);
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                view=LayoutInflater.from(activity).inflate(R.layout.info_window_on_map,null);
                return view;
            }
        });

        //-----------------------Go to get the details activity (EachPetActivity) of this pet
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Pet pet=pets.get(markers.indexOf(marker));
                pet.bitmap=null;
                Intent deatails=new Intent(activity,EachPetActivity.class);
                deatails.putExtra("thisPet",pet);
                startActivity(deatails);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final Pet pet=pets.get(markers.indexOf(marker));
                //------------------Go to get the image of this pet If the image has not been loaded
                if(pet.bitmap==null) {
                        firebaseStorage.getReference().child(pet.getPhotoUrl()).getBytes(Long.MAX_VALUE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        pet.bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        marker.hideInfoWindow();
                                        marker.showInfoWindow();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                        });
                }
                marker.showInfoWindow();
                return true;
            }
        });

    }
}
