package com.ldsilver.task2final;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import io.grpc.Context;


public class MainActivity extends AppCompatActivity {
    EditText mEmailAddress, mPassword, mName, mHeight, mWeight, mCalories, mGoalWeight;
    Button mLogOut,mChangeProfileImage;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UserID;
    ImageView mProfileImage;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mName= findViewById(R.id.Name);
        mHeight= findViewById(R.id.Height);
        mWeight= findViewById(R.id.Weight);
        mEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        mPassword= findViewById(R.id.editTextTextPassword2);
        mLogOut= findViewById(R.id.buttonLogOut);
        mCalories= findViewById(R.id.Calories);
        mGoalWeight=findViewById(R.id.GoalWeight);
        mProfileImage= findViewById(R.id.ProfileImage);
        mChangeProfileImage= findViewById(R.id.changeProfile);

        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

        StorageReference profileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mProfileImage);
            }
        });

        UserID= fAuth.getCurrentUser().getUid();

        DocumentReference documentReference= fStore.collection("users").document(UserID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {


                mName.setText(documentSnapshot.getString("Name"));
                mHeight.setText(documentSnapshot.getString("Height"));
                mWeight.setText(documentSnapshot.getString("Weight"));
                mGoalWeight.setText(documentSnapshot.getString("GoalWeight"));
                mCalories.setText(documentSnapshot.getString("CaloricIntake"));
                mEmailAddress.setText(documentSnapshot.getString("Email"));


            }
        });

        mChangeProfileImage.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                Intent i= new Intent(view.getContext(),EditProfile.class);
                i.putExtra("CaloricIntake",mCalories.getText().toString());
                i.putExtra("Email",mEmailAddress.getText().toString());
                i.putExtra("GoalWeight",mGoalWeight.getText().toString());
                i.putExtra("Height",mHeight.getText().toString());
                i.putExtra("Name",mName.getText().toString());
                i.putExtra("Weight",mWeight.getText().toString());


                startActivity(i);


            }
        }));


    }





    public void logout(View view){
        FirebaseAuth.getInstance().signOut();//logout user
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}