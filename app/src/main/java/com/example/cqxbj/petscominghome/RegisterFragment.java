package com.example.cqxbj.petscominghome;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment implements View.OnClickListener{

    //---------Firebase
    FirebaseFirestore firebaseDb;
    FirebaseAuth firebaseAuth;

    //---------Activity
    MainActivity activity;

    //---------UI widgets
    Button signUp;
    TextView signInTextView;
    TextView signUpTextView;
    ProgressBar progressBar;
    EditText registerUsername;
    EditText registerEmail;
    EditText registerPassword;
    EditText registerPasswordConfirm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_register,container,false);
        activity=(MainActivity)getActivity();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.hideTheInput();
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDb=FirebaseFirestore.getInstance();


        progressBar=view.findViewById(R.id.progressBarRegister);
        signUpTextView=view.findViewById(R.id.signUpTextView);
        signInTextView=view.findViewById(R.id.signIn);
        signInTextView.setOnClickListener(this);
        registerUsername=view.findViewById(R.id.registerUsername);
        registerEmail=view.findViewById(R.id.registerEmail);
        registerPassword=view.findViewById(R.id.registerPassword);
        registerPasswordConfirm=view.findViewById(R.id.registerPasswordConfim);

        signUp=view.findViewById(R.id.registerBtn);
        signUp.setOnClickListener(this);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignInPage();
            }
        });
        return view;
    }
    //------------------Register
    public void register()
    {
        if(isInformationFilled())
        {
            if(isPasswordConfirmed())
            {
                progressBar.setVisibility(View.VISIBLE);
                hideWidgets();

                firebaseAuth.createUserWithEmailAndPassword(registerEmail.getText().toString(),registerPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()) {
                                    task.getResult().getUser()
                                            .updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(registerUsername.getText().toString()).build());
                                    Toast.makeText(getContext(),"Registered successfully",Toast.LENGTH_LONG).show();
                                    activity.hideAllfragments();
                                    activity.showTheFragment(activity.petsListFragment);
                                    activity.petsListFragment.getDefault();
                                    activity.getSupportActionBar().setTitle("Pets");
                                    activity.setUI(firebaseAuth.getCurrentUser());
                                }
                                else {

                                    Toast.makeText(getContext(),task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                displayWidgets();

                            }
                        });
            }
            else
            {
                Toast.makeText(getContext(),"Passwords are different",Toast.LENGTH_LONG).show();
            }


        }
        else
        {
            Toast.makeText(getContext(),"Please fill the form",Toast.LENGTH_LONG).show();
        }


    }

    public void goToSignInPage()
    {
        activity.hideTheInput();
        activity.hideTheFragment(activity.registerFragment);
        activity.getSupportActionBar().setTitle("Sign in");
        activity.showTheFragment(activity.loginFragment);
    }


    public boolean isInformationFilled()
    {
        if(registerUsername.getText().toString().equals("")||registerEmail.getText().toString().equals("")
                ||registerPassword.getText().toString().equals("")||registerPasswordConfirm.getText().toString().equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean isPasswordConfirmed()
    {
        if(registerPassword.getText().toString().equals(registerPasswordConfirm.getText().toString()))
        {
            return true;
        }
        else return false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signIn:
                goToSignInPage();
                break;
            case R.id.registerBtn:
                register();
                activity.hideTheInput();
                break;
        }
    }

    public void hideWidgets()
    {
        signUpTextView.setVisibility(View.INVISIBLE);
        signInTextView.setVisibility(View.INVISIBLE);
        signUp.setVisibility(View.INVISIBLE);
        registerUsername.setVisibility(View.INVISIBLE);
        registerEmail.setVisibility(View.INVISIBLE);
        registerPassword.setVisibility(View.INVISIBLE);
        registerPasswordConfirm.setVisibility(View.INVISIBLE);

    }
    public void displayWidgets()
    {
        signUpTextView.setVisibility(View.VISIBLE);
        signInTextView.setVisibility(View.VISIBLE);
        signUp.setVisibility(View.VISIBLE);
        registerUsername.setVisibility(View.VISIBLE);
        registerEmail.setVisibility(View.VISIBLE);
        registerPassword.setVisibility(View.VISIBLE);
        registerPasswordConfirm.setVisibility(View.VISIBLE);
    }

    public void resetPage()
    {
        registerEmail.setText("");
        registerPassword.setText("");
        registerPasswordConfirm.setText("");
        registerUsername.setText("");
    }

    //--------------Reset when the fragment is hidden
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden)
        {
            resetPage();
        }
    }
}
