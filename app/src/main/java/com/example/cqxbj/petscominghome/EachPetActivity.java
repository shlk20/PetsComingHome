package com.example.cqxbj.petscominghome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class EachPetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_pet);
        int ImageUrl=getIntent().getIntExtra("ImageUrl",R.drawable.sample);
        ImageView imagePet=findViewById(R.id.imagePet);
        imagePet.setImageResource(ImageUrl);
        
    }
}
