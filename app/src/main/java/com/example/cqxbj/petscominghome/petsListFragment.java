package com.example.cqxbj.petscominghome;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cqxbj on 22/05/18.
 */

public class petsListFragment extends Fragment{
    ListView petList;
    ArrayList<Pet> pets = new ArrayList<Pet>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.pets_list,container,false);

        pets.add(new Pet("A", R.drawable.sample));
        pets.add(new Pet("B", R.drawable.sample2));
        pets.add(new Pet("C", R.drawable.sample3));
        pets.add(new Pet("D", R.drawable.sample4));

        PetAdapter petAdapter = new PetAdapter(getContext(), R.layout.list_item, pets);
        petList = (ListView) view.findViewById(R.id.petList);
        petList.setAdapter(petAdapter);
        return view;
    }
    class PetAdapter extends ArrayAdapter<Pet> {
        int rescourceid;

        public PetAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            rescourceid = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            final Pet pet = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(rescourceid, parent, false);
            view.setClickable(false);


            TextView name = (TextView) view.findViewById(R.id.NameTxt);
            name.setText(pet.name);

            ImageView image = (ImageView) view.findViewById(R.id.imagePet);
            image.setImageResource(pet.ImageUrl);


            TextView goToDetails = (TextView) view.findViewById(R.id.Details);
            goToDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailsIntent=new Intent(getContext(),EachPetActivity.class);
                    detailsIntent.putExtra("ImageUrl",pet.ImageUrl);
                    startActivity(detailsIntent);
                }
            });

            return view;
        }


    }
}
