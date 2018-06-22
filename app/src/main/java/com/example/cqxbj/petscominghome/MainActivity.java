package com.example.cqxbj.petscominghome;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private RequestManager rm;



    GoogleSignInClient googleSignInClient;
    SharedPreferences sp;

    petsListFragment petsListFragment;
    AddNewPetFragment addNewPetFragment;
    AppSettingFragment appSettingFragment;
    loginFragment loginFragment;
    RegisterFragment registerFragment;
    SearchFragment searchFragment;
    ShowOnMapFragment showOnMapFragment;
     ArrayList<Fragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rm=Glide.with(this);


        petsListFragment=new petsListFragment();
        showOnMapFragment=new ShowOnMapFragment();
        addNewPetFragment=new AddNewPetFragment();
        loginFragment=new loginFragment();
        registerFragment=new RegisterFragment();
        searchFragment=new SearchFragment();
        appSettingFragment=new AppSettingFragment();
        fragments=new ArrayList<Fragment>();
        fragments.add(petsListFragment);
        fragments.add(showOnMapFragment);
        fragments.add(addNewPetFragment);
        fragments.add(loginFragment);
        fragments.add(registerFragment);
        fragments.add(searchFragment);
        fragments.add(appSettingFragment);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(this,gso);

        sp=getSharedPreferences("AppSettings",0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.MainDrawerLayout);
        mNavigationView = findViewById(R.id.MainNavigationView);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                petsListFragment.resetMode();
                petsListFragment.refreshBtn.setVisibility(View.VISIBLE);
                hideAllfragments();
                switch(item.getItemId())
                {
                    case R.id.PetsItem:
                                showTheFragment(petsListFragment);
                                petsListFragment.getDefault();
                                getSupportActionBar().setTitle("Pets around you");
                                break;
                    case R.id.SignInItem:
                                showTheFragment(loginFragment);
                                getSupportActionBar().setTitle("Sign in");
                                break;
                    case R.id.SearchItem:
                                showTheFragment(searchFragment);
                                getSupportActionBar().setTitle("Search");
                                break;
                    case R.id.SignUpItem:
                                showTheFragment(registerFragment);
                                getSupportActionBar().setTitle("Sign up");
                                break;
                    case R.id.AddNewPetItem:
                                showTheFragment(addNewPetFragment);
                                getSupportActionBar().setTitle("Add a new pet");
                                 break;
                    case R.id.ShowOnMapItem:
                                showTheFragment(showOnMapFragment);
                                getSupportActionBar().setTitle("Map");
                                break;
                    case R.id.AppSettingItem:
                                showTheFragment(appSettingFragment);
                                getSupportActionBar().setTitle("App Settings");
                                break;
                    case R.id.MyPetsItem:
                                showTheFragment(petsListFragment);
                                petsListFragment.getMyPets();
                                getSupportActionBar().setTitle("My pets");
                                break;
                    case R.id.SignOutItem:
                                firebaseAuth.signOut();
                                if(GoogleSignIn.getLastSignedInAccount(getApplicationContext())!=null)
                                {
                                 googleSignInClient.signOut();
                                }
                                setUI(firebaseAuth.getCurrentUser());
                                getSupportActionBar().setTitle("Pets around you");
                                showTheFragment(petsListFragment);
                                petsListFragment.getDefault();
                                break;
                    }
                    return true;
            }
        });

        showTheFragment(petsListFragment);
        getSupportActionBar().setTitle("Pets around you");
        setUI(user);
    }

    @Override
    public void onBackPressed() {
        if(getSupportActionBar().getTitle().toString().equals("Pets around you"))
        {
            super.onBackPressed();
        }
        else {
            hideAllfragments();
            getFragmentManager().beginTransaction().show(petsListFragment).commit();
            petsListFragment.resetMode();
            petsListFragment.getDefault();
            getSupportActionBar().setTitle("Pets around you");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
        return true;
    }

    public void setUI(FirebaseUser user)
    {
        ImageView userImage=mNavigationView.getHeaderView(0).findViewById(R.id.userImage);
        TextView  userName=mNavigationView.getHeaderView(0).findViewById(R.id.userNameInNavHeader);
        TextView  userEmail=mNavigationView.getHeaderView(0).findViewById(R.id.userEmInNavHeader);
        if(user!=null)
        {
            if(user.getPhotoUrl()!=null) {
                rm.load(user.getPhotoUrl()).into(userImage);
            }
            else {
                userImage.setImageResource(R.drawable.ic_android_green_24dp);
            }

            if(user.getDisplayName()!=null) {
                userName.setText(user.getDisplayName());
            }
            else {
                userName.setText("");
            }

            if(user.getEmail()!=null) {
                userEmail.setText(user.getEmail());
            }else{
                userEmail.setText("");
            }
            mNavigationView.getMenu().findItem(R.id.MyPetsItem).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.SignInItem).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.SignUpItem).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.AddNewPetItem).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.SignOutItem).setVisible(true);

        }
        else {
            userImage.setImageResource(R.drawable.ic_android_green_24dp);
            userEmail.setText("Email");
            userName.setText("Username");
            mNavigationView.getMenu().findItem(R.id.MyPetsItem).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.SignInItem).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.SignUpItem).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.AddNewPetItem).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.SignOutItem).setVisible(false);
        }

    }

    public void hideAllfragments()
    {
        for(Fragment f:fragments)
        {
            if(f.isAdded()) {
                getFragmentManager().beginTransaction().hide(f).commit();
            }
        }
    }

    public void showTheFragment(Fragment fragment)
    {
        if(!fragment.isAdded())
        {
            getFragmentManager().beginTransaction().add(R.id.MainContainer,fragment).commit();
        }
        else
        {
            getFragmentManager().beginTransaction().show(fragment).commit();
        }
    }

    public void hideTheInput()
    {
        InputMethodManager im=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(im.isActive()) im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

    }
}
