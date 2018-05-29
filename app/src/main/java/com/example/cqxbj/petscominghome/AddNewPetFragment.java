package com.example.cqxbj.petscominghome;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Calendar;

import javax.xml.transform.Result;

/**
 * Created by cqxbj on 22/05/18.
 */

public class AddNewPetFragment extends Fragment implements View.OnClickListener,DatePickerDialog.OnDateSetListener {

    final int SELECT_PHOTO=1;
    View view;
    Spinner spinnerForStatus;
    Spinner spinnerForType;
    Spinner spinnerForGender;
    Spinner spinnerForDesex;

    ArrayAdapter<String> statusAdapter;
    ArrayAdapter<String> typeAdapter;
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> desexAdaper;

    String[] statusData={"Good","Great","Choose status"};
    String[] typeData={"Dog","Cat","Pig","Rabbit","Choose pet type"};
    String[] genderData={"Male","Female","Choose pet gender"};
    String[] deData={"Yes","No","Is your pet desexed?"};

    TextView ChooseDate;
    int day,year,month;
    DatePickerDialog datePickerDialog;

    Button uploadPhotoButton;
    TextView photoTextView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //datepickerDialog for choosing date
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
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_add_new_pet,container,false);

        spinnerForStatus=(Spinner) view.findViewById(R.id.spinnerForStatus);
        spinnerForStatus.setAdapter(statusAdapter);
        spinnerForStatus.setSelection(statusData.length-1);

        spinnerForType=(Spinner) view.findViewById(R.id.spinnerForType);
        spinnerForType.setAdapter(typeAdapter);
        spinnerForType.setSelection(typeData.length-1);

        spinnerForGender=(Spinner) view.findViewById(R.id.spinnerForGender);
        spinnerForGender.setAdapter(genderAdapter);
        spinnerForGender.setSelection(genderData.length-1);

        spinnerForDesex=(Spinner)view.findViewById(R.id.spinnerForDe);
        spinnerForDesex.setAdapter(desexAdaper);
        spinnerForDesex.setSelection(deData.length-1);

        ChooseDate=(TextView) view.findViewById(R.id.ChooseDate);
        ChooseDate.setOnClickListener(this);


        photoTextView=(TextView)view.findViewById(R.id.photoTextView);
        uploadPhotoButton=(Button) view.findViewById(R.id.uploadPhotoBtn);
        uploadPhotoButton.setOnClickListener(this);


        return view;
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
        }
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ChooseDate.setText(month+"_"+dayOfMonth+"_"+year);
    }

    public void selectPhoto()
    {
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {
            case SELECT_PHOTO:
                if(resultCode== Activity.RESULT_OK)
                {
                    Uri uri=data.getData();
                    photoTextView.setText(uri.toString());
                    break;
                }

        }
    }
}
