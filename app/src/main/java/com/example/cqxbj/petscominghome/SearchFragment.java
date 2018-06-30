package com.example.cqxbj.petscominghome;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SearchFragment extends Fragment implements View.OnClickListener,DatePickerDialog.OnDateSetListener{

    TextView searchName;
    TextView searchChip;
    TextView searchBreed;
    TextView searchDate;
    Button searchBtn;

    //Spinner for choosing the kind
    Spinner searchKind;
    String[] kindData={"Type","Dog","Cat","Bird","Pig","Reptile","Rodent","Others"};
    ArrayAdapter<String> kindAdapter;
    //Searching value of kind
    String searchKindValue="";

    //Spinner for choosing the size
    Spinner searchSize;
    String[] sizeData={"Size","Small","Medium", "Large"};
    ArrayAdapter<String> sizeAdapter;
    //Searching value of size
    String searchSizeValue="";

    //Spinner for choosing the region
    Spinner searchCity;
    String[] cityData={"Location","Auckland","Hamilton","Taurange"
                        ,"Rotorua","Gisborne","Napier"
                        ,"New Plymounth","Palmerston North","Wellington","Christchurch",
                        "Dunedin","Queenstown","Invercargill"};
    ArrayAdapter<String> cityAdapter;
    //Searching value of region
    String searchLocationValue="";

    //Date
    DatePickerDialog datePickerDialog;
    Long searchDateValue;

    //Activity
    MainActivity activity;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_search,container,false);
        activity=(MainActivity) getActivity();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.hideTheInput();
            }
        });

        //--------------------TextView
        searchName=view.findViewById(R.id.searchName);
        searchChip=view.findViewById(R.id.searchChipNumber);
        searchKind=view.findViewById(R.id.searchKind);
        searchBreed=view.findViewById(R.id.searchBreed);
        searchBtn=view.findViewById(R.id.searchBtn);

        //--------------------Spinners
        searchSize=view.findViewById(R.id.searchSize);
        sizeAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,sizeData);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSize.setAdapter(sizeAdapter);
        searchSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    searchSizeValue="";
                }
                else
                {
                    searchSizeValue=searchSize.getItemAtPosition(position).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        searchSize.setSelection(0);

        searchKind=view.findViewById(R.id.searchKind);
        kindAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,kindData);
        kindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchKind.setAdapter(kindAdapter);
        searchKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    searchKindValue="";
                }
                else
                {
                    searchKindValue=searchKind.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        searchKind.setSelection(0);

        searchCity=view.findViewById(R.id.searchCity);
        cityAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,cityData);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchCity.setAdapter(cityAdapter);
        searchCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    searchLocationValue="";
                }
                else
                {
                    searchLocationValue=searchCity.getItemAtPosition(position).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        searchCity.setSelection(0);


        searchDate=view.findViewById(R.id.searchDate);
        searchDate.setOnClickListener(this);

        //--------------------DatePickerDialog
        Calendar c=Calendar.getInstance();

        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog=new DatePickerDialog(getContext(),this,year,month,day);

        //-----Go to search these pets
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------- search pets
                if(searchDateValue==null)
                    {
                        searchDateValue=0L;
                    }
                    activity.petsListFragment.getSearchPets(
                        searchName.getText().toString(),
                        searchBreed.getText().toString(),
                        searchSizeValue,
                        searchChip.getText().toString(),
                        searchKindValue,
                        searchLocationValue,
                        searchDateValue);

                //-----hide the search fragment and show the pets list fragment
                activity.hideTheFragment(activity.searchFragment);
                activity.showTheFragment(activity.petsListFragment);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.searchDate:
                datePickerDialog.show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        searchDateValue=new GregorianCalendar(year,month,dayOfMonth).getTimeInMillis();
        searchDate.setText(dayOfMonth+"/"+month+"/"+year);
    }

    public void resetPage()
    {
        searchName.setText("");
        searchBreed.setText("");
        searchChip.setText("");
        searchKind.setSelection(0);
        searchSize.setSelection(0);
        searchCity.setSelection(0);
        searchDate.setText("Choose a date");
        searchDateValue=null;
    }

    //-----------------------Reset when the fragment is hidden
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden)
        {
            resetPage();
        }
    }


}
