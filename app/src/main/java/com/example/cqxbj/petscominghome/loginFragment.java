package com.example.cqxbj.petscominghome;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by cqxbj on 22/05/18.
 */

public class loginFragment extends Fragment implements View.OnClickListener{
    FirebaseAuth firebaseAuth;

    Activity activity;
    FragmentManager fragmentManager;
    TextView signUpTextView;
    EditText signInEmail;
    EditText signInPassword;
    Button signInBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login,container,false);
        firebaseAuth=FirebaseAuth.getInstance();
        signUpTextView=(TextView)view.findViewById(R.id.signUpTextView);
        signUpTextView.setOnClickListener(this);
        signInEmail=(EditText)view.findViewById(R.id.signInEmail);
        signInPassword=(EditText)view.findViewById(R.id.signInPassword);
        signInBtn=(Button)view.findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(this);

        return view;
    }
    public void signIn()
    {

        if(!signInEmail.getText().toString().equals("") && !signInPassword.getText().toString().equals("")) {
           signInBtn.setClickable(false);
            firebaseAuth.signInWithEmailAndPassword(signInEmail.getText().toString(), signInPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           signInBtn.setClickable(true);
                            if (task.isSuccessful()) {

                                activity = getActivity();
                                activity.recreate();
                            } else {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signInBtn.setClickable(true);
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            Toast.makeText(getContext(),"Please input the email and password",Toast.LENGTH_LONG).show();
        }

    }
    public void goToSignUp()
    {
        activity=getActivity();
        fragmentManager=activity.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.MainContainer,new RegisterFragment()).commit();
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
                break;
        }
    }
}
