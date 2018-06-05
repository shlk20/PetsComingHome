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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment implements View.OnClickListener{

    FirebaseFirestore firebaseDb;
    FirebaseAuth firebaseAuth;

    Activity activity;
    Button signUp;
    TextView signIn;
    EditText registerUsername;
    EditText registerEmail;
    EditText registerPhone;
    EditText registerPassword;
    EditText registerPasswordConfirm;
    FragmentManager fragmentManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.register,container,false);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDb=FirebaseFirestore.getInstance();


        signIn=view.findViewById(R.id.signIn);
        signIn.setOnClickListener(this);
        registerUsername=(EditText)view.findViewById(R.id.signInPassword);
        registerEmail=(EditText)view.findViewById(R.id.registerEmail);
        registerPhone=(EditText)view.findViewById(R.id.signInEmail);
        registerPassword=(EditText)view.findViewById(R.id.registerPassword);
        registerPasswordConfirm=(EditText)view.findViewById(R.id.registerPasswordConfim);

        signUp=(Button)view.findViewById(R.id.registerBtn);
        signUp.setOnClickListener(this);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignInPage();
            }
        });
        return view;
    }
    public void register()
    {
        if(isInformationFilled()) {
            firebaseAuth.createUserWithEmailAndPassword(registerEmail.getText().toString(),registerPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Map<String,String> userMap=new HashMap<>();
                            userMap.put("Username",registerUsername.getText().toString());
                            userMap.put("Email",registerEmail.getText().toString());
                            userMap.put("Phone",registerPhone.getText().toString());
                            userMap.put("Uid",firebaseAuth.getCurrentUser().getUid());
                            firebaseDb.collection("User").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful())
                                    {
                                        activity=getActivity();
                                        activity.recreate();
                                        //fragmentManager=activity.getFragmentManager();
                                        //fragmentManager.beginTransaction().replace(R.id.MainContainer,new petsListFragment()).commit();
                                    }
                                    else
                                    {
                                        Toast.makeText(getContext(),"Failed to register",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Failed to register",Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            Toast.makeText(getContext(),"Please fill the information",Toast.LENGTH_LONG).show();
        }


    }

    public void goToSignInPage()
    {
        activity=getActivity();
        fragmentManager=activity.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.MainContainer,new loginFragment()).commit();
    }


    public boolean isInformationFilled()
    {
        if(registerUsername.getText().toString().equals("")||registerEmail.getText().toString().equals("")
                ||registerPassword.getText().toString().equals("")||registerPasswordConfirm.getText().toString().equals("")||
                registerPhone.getText().toString().equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
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
                break;
        }
    }


}
