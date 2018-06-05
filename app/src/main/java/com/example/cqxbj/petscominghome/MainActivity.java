package com.example.cqxbj.petscominghome;


import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
   private FragmentManager fragmentManager;

    // a static variable to get a reference of our application context
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

                switch(item.getItemId())
                {
                    case R.id.SignInItem:
                            fragmentManager.beginTransaction().replace(R.id.MainContainer,new loginFragment())
                                    .addToBackStack(null).commit();
                            getSupportActionBar().setTitle("Sign In");
                            break;

                    case R.id.SignUpItem:
                            fragmentManager.beginTransaction().replace(R.id.MainContainer,new RegisterFragment())
                                    .addToBackStack(null).commit();
                            getSupportActionBar().setTitle("Register");
                            break;
                    case R.id.AddNewPetItem:
                            fragmentManager.beginTransaction().replace(R.id.MainContainer,new AddNewPetFragment())
                                    .addToBackStack(null).commit();
                            getSupportActionBar().setTitle("Add a new pet");
                            break;
                    case R.id.SignOutItem:
                            firebaseAuth.signOut();
                            recreate();
                            break;
                    }
                return true;
            }
        });
        fragmentManager=getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.MainContainer,new petsListFragment()).commit();

        setUI(user);

        contextOfApplication = getApplicationContext();
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
        if(user!=null) {
            mNavigationView.getMenu().findItem(R.id.SignInItem).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.SignUpItem).setVisible(false);
            ((TextView)mNavigationView.getHeaderView(0).findViewById(R.id.userEmInNavHeader)).setText(user.getEmail());


        }
        else {
            //mNavigationView.getMenu().findItem(R.id.AddNewPetItem).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.SignOutItem).setVisible(false);
        }
    }

}
