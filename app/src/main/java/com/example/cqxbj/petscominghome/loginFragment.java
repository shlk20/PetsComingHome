package com.example.cqxbj.petscominghome;

import android.app.Fragment;
import android.content.Intent;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by cqxbj on 22/05/18.
 */

public class loginFragment extends Fragment implements View.OnClickListener{
    //---------Firebase
    FirebaseAuth firebaseAuth;

    //--------Activity
    MainActivity activity;

    //--------UI widgets
    TextView signInTextView;
    TextView signUpTextView;
    EditText signInEmail;
    EditText signInPassword;
    Button signInBtn;
    SignInButton googleSignInBtn;
    ProgressBar progressBar;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login,container,false);
        activity=(MainActivity) getActivity();

        //Clicking the view can hide the input
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.hideTheInput();
            }
        });


        firebaseAuth=FirebaseAuth.getInstance();
        signInTextView=view.findViewById(R.id.signInTextView);
        signUpTextView=view.findViewById(R.id.signUpTextView);
        signUpTextView.setOnClickListener(this);
        signInEmail=view.findViewById(R.id.signInEmail);
        signInPassword=view.findViewById(R.id.registerUsername);
        signInBtn=view.findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(this);
        googleSignInBtn=view.findViewById(R.id.googleSignInBtn);
        googleSignInBtn.setOnClickListener(this);
        progressBar=view.findViewById(R.id.progressBar);


//
//        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        googleSignInClient= GoogleSignIn.getClient(getContext(),gso);
        return view;
    }
    //------Sign in with your Email and Password
    public void signIn()
    {

        if(!signInEmail.getText().toString().equals("") && !signInPassword.getText().toString().equals("")) {
            hideWidgets();
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(signInEmail.getText().toString(), signInPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                activity.hideAllfragments();
                                activity.showTheFragment(activity.petsListFragment);
                                activity.petsListFragment.getDefault();
                                activity.getSupportActionBar().setTitle("Pets around you");
                                activity.setUI(firebaseAuth.getCurrentUser());
                            } else {
                                Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            displayWidgets();
                        }
                    });
        }
        else
        {
            Toast.makeText(activity,"Please input the email and password",Toast.LENGTH_LONG).show();
        }

    }
    //---------------------Sign in with Google
    public void signInWithGoogle()
    {
        Intent signInIntent = activity.googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseSignInWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(activity,"Error",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void firebaseSignInWithGoogle(final GoogleSignInAccount account)
    {
        hideWidgets();
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                              activity=(MainActivity) getActivity();
                              activity.hideAllfragments();
                              activity.showTheFragment(activity.petsListFragment);
                              activity.petsListFragment.getDefault();
                              activity.getSupportActionBar().setTitle("Pets around you");
                              activity.setUI(firebaseAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        displayWidgets();

                    }
                });
    }


    public void goToSignUp()//go to the Signup fragment.
    {
        activity.hideTheInput();
        activity.getSupportActionBar().setTitle("Sign up");
        activity.hideTheFragment(activity.loginFragment);
        activity.showTheFragment(activity.registerFragment);
    }

    public void displayWidgets()
    {
            googleSignInBtn.setVisibility(View.VISIBLE);
            signInTextView.setVisibility(View.VISIBLE);
            signUpTextView.setVisibility(View.VISIBLE);
            signInBtn.setVisibility(View.VISIBLE);
            signInEmail.setVisibility(View.VISIBLE);
            signInPassword.setVisibility(View.VISIBLE);

    }
    public void hideWidgets()
    {
        googleSignInBtn.setVisibility(View.INVISIBLE);
        signInTextView.setVisibility(View.INVISIBLE);
        signUpTextView.setVisibility(View.INVISIBLE);
        signInBtn.setVisibility(View.INVISIBLE);
        signInEmail.setVisibility(View.INVISIBLE);
        signInPassword.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {

            case R.id.signUpTextView:
                goToSignUp();
                break;
            case R.id.signInBtn:
                signIn();
                activity.hideTheInput();
                break;
            case R.id.googleSignInBtn:
                signInWithGoogle();
                activity.hideTheInput();
                break;
        }
    }

    public void resetPage()
    {
        signInEmail.setText("");
        signInPassword.setText("");
    }

    //----------------Reset when the fragment is hidden
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden)
        {
            resetPage();
        }
    }
}
