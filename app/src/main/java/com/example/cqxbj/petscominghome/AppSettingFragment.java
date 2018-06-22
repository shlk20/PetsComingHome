package com.example.cqxbj.petscominghome;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class AppSettingFragment extends Fragment implements View.OnClickListener {

    TextView searchRadius;
    Switch lostSwitch;
    Switch foundSwitch;
    int radius;
    Boolean lostBool;
    Boolean foundBool;
    Button saveBtn;
    MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_app_setting,container,false);

        activity=(MainActivity) getActivity();
        radius=activity.sp.getInt("Radius",1);
        lostBool=activity.sp.getBoolean("LostBool",true);
        foundBool=activity.sp.getBoolean("FoundBool",true);

        searchRadius=view.findViewById(R.id.searchRadius);
        lostSwitch=view.findViewById(R.id.lostSwitch);
        foundSwitch=view.findViewById(R.id.foundSwitch);

        saveBtn=view.findViewById(R.id.saveSettingsBtn);
        saveBtn.setOnClickListener(this);

        setUI();
        return view;
    }

    private void setUI()
    {
        lostSwitch.setChecked(lostBool);
        foundSwitch.setChecked(foundBool);
        searchRadius.setText(String.valueOf(radius));

    }

    private void saveChanges()
    {
        SharedPreferences.Editor editor=activity.sp.edit();
        editor.putBoolean("LostBool",lostSwitch.isChecked());
        editor.putBoolean("FoundBool",foundSwitch.isChecked());
        editor.putInt("Radius",Integer.parseInt(searchRadius.getText().toString()));
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        InputMethodManager im=(InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(im.isActive()) im.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        saveChanges();
        activity.hideAllfragments();
        activity.getFragmentManager().beginTransaction().show(activity.petsListFragment).commit();
        activity.petsListFragment.getDefault();
        activity.getSupportActionBar().setTitle("Pets around you");
    }
}
