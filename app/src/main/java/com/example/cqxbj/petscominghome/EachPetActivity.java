package com.example.cqxbj.petscominghome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EachPetActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseStorage firebaseStorage;

    TextView nameText;
    TextView kindText;
    TextView breedText;
    TextView ageText;
    TextView genderText;
    TextView sizeText;
    TextView statusText;
    TextView dateText;
    TextView desexedText;
    TextView colorText;
    TextView locationText;
    TextView descriptionText;
    ImageView imagePet;
    Button commentBtn;
    Pet thisPet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_pet);

        firebaseStorage=FirebaseStorage.getInstance();
        Serializable getS=getIntent().getSerializableExtra("thisPet");
        thisPet=(Pet)getS;

        setText(thisPet);
        loadImage(thisPet);

        commentBtn=(Button)findViewById(R.id.addCommentBtn);
        commentBtn.setOnClickListener(this);
    }


    private void setText(Pet pet)
    {

        nameText=findViewById(R.id.detailNameText);
        kindText=findViewById(R.id.detailKindText);
        breedText=findViewById(R.id.detailBreedText);
        ageText=findViewById(R.id.detailAgeText);
        genderText=findViewById(R.id.detailGenderText);
        sizeText=findViewById(R.id.detailSizeText);
        statusText=findViewById(R.id.detailStatusText);
        dateText=findViewById(R.id.detailMissingDateText);
        colorText=findViewById(R.id.detailColorText);
        locationText=findViewById(R.id.detailLocationText);
        desexedText=findViewById(R.id.detailDesexedText);
        descriptionText=findViewById(R.id.detailDescriptionText);

        nameText.setText(pet.getName());
        kindText.setText(pet.getKind());
        breedText.setText(pet.getBreed());
        ageText.setText(pet.getAge().toString());
        genderText.setText(pet.getGender());
        sizeText.setText(pet.getSize());
        statusText.setText(pet.getStatus());
        colorText.setText(pet.getColor());
        locationText.setText(pet.getRegion());
        desexedText.setText(pet.getDesexed());
        descriptionText.setText(pet.getDescription());
        

        Date thisDate=new Date(pet.getDate());
        DateFormat simpleDateFormat= SimpleDateFormat.getDateInstance();
        String dateString=simpleDateFormat.format(thisDate);
        dateText.setText(dateString);

    }

    private void loadImage(Pet pet)
    {
        imagePet=findViewById(R.id.imagePet);
        firebaseStorage.getReference()
            .child(pet.getPhotoUrl()).getBytes(Long.MAX_VALUE)
            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imagePet.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
    }


    @Override
    public void onClick(View v) {
        Intent commentsOnthisPet=new Intent(getApplicationContext(),CommentsActivity.class);
        commentsOnthisPet.putExtra("thisPet",thisPet);
        startActivity(commentsOnthisPet);

    }
}
