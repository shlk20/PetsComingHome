package com.example.cqxbj.petscominghome;

import android.content.Context;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener,OnCompleteListener<QuerySnapshot>{

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    ListView commentsListView;
    TextView addCommentTextView;
    Button addBtn;
    Button cancelBtn;
    FloatingActionButton floatingActionButton;
    ProgressBar pb;

    Pet pet;

    ArrayAdapter<comment> arrayAdapter;

    ArrayList<comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        pet=(Pet)getIntent().getSerializableExtra("thisPet");
        commentsListView=findViewById(R.id.commentsListView);
        addCommentTextView=findViewById(R.id.addCommentText);
        addCommentTextView.setVisibility(View.INVISIBLE);
        addBtn=findViewById(R.id.addCommentBtn);
        addBtn.setOnClickListener(this);
        addBtn.setVisibility(View.INVISIBLE);
        floatingActionButton=findViewById(R.id.addCommentFAB);
        floatingActionButton.setOnClickListener(this);
        pb=findViewById(R.id.progressBarOnComment);
        pb.setVisibility(View.INVISIBLE);
        cancelBtn=findViewById(R.id.cancelBtnOnComment);
        cancelBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setOnClickListener(this);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        firestore.collection("Pet")
                .document(pet.getId())
                .collection("Comment").orderBy("Date", Query.Direction.DESCENDING).get().addOnCompleteListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addCommentFAB:
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    commentsListView.setVisibility(View.INVISIBLE);
                    addCommentTextView.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please login first",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.addCommentBtn:
                if(!addCommentTextView.getText().toString().equals("")) {

                    addComment();
                }else
                {
                    Toast.makeText(getApplicationContext(),"Please fill in the comment",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancelBtnOnComment:
                commentsListView.setVisibility(View.VISIBLE);
                addCommentTextView.setVisibility(View.INVISIBLE);
                addBtn.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if(task.isSuccessful())
        {

            comments=new ArrayList<comment>();
            for (QueryDocumentSnapshot eachComments:task.getResult())
            {
                comments.add(
                        new comment(eachComments.getData().get("UserDisplayName").toString()
                                ,eachComments.getData().get("Text").toString()
                                ,(Long) eachComments.getData().get("Date"))
                );

            }
            arrayAdapter=new commentAdapter(getApplicationContext(),R.layout.each_comment,comments);
            commentsListView.setAdapter(arrayAdapter);
        }else
        {
            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void addComment()
    {

        pb.setVisibility(View.VISIBLE);
        Map<String,Object> newComment =new HashMap<String,Object>();
        newComment.put("UserEmail",firebaseAuth.getCurrentUser().getEmail());
        newComment.put("UserDisplayName",firebaseAuth.getCurrentUser().getDisplayName());
        newComment.put("Text",addCommentTextView.getText().toString());
        newComment.put("Date",System.currentTimeMillis());

        firestore.collection("Pet").document(pet.getId()).collection("Comment")
                .add(newComment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    recreate();
                }
                else
                {
                    pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class commentAdapter extends ArrayAdapter<comment>
    {

        int re;
        public commentAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            re=resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            if(convertView==null)
            {
                view = LayoutInflater.from(getContext()).inflate(re, parent, false);
            }
            else
            {
                view=convertView;
            }
            TextView DisplayName=view.findViewById(R.id.commentEmail);
            DisplayName.setText(getItem(position).DisplayName);

            TextView Comment=view.findViewById(R.id.commentText);
            Comment.setText(getItem(position).Text);

            TextView Date=view.findViewById(R.id.commentDate);


            Date thisDate=new Date(getItem(position).Date);
            DateFormat simpleDateFormat=SimpleDateFormat.getDateTimeInstance();
            String dateString=simpleDateFormat.format(thisDate);
            Date.setText(dateString);
            return view;

        }
    }

    class comment
    {
        comment(String DisplayName,String Text,Long Date)
        {
            this.DisplayName=DisplayName;
            this.Text=Text;
            this.Date=Date;
        }
        String DisplayName;
        String Text;
        Long Date;
    }
}
