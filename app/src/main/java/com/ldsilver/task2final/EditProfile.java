package com.ldsilver.task2final;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    public static final
    String TAG = "TAG";

    EditText mEmailAddress,  mName, mHeight, mWeight, mCalories, mGoalWeight;
    Button mSave;
    ToggleButton mToggleUnit;
    FirebaseAuth fAuth;
    TextView mLoginButton;
    FirebaseFirestore fStore;
    String UserID;
    ImageView mProfileImage;
    FirebaseUser user;
    StorageReference storageReference;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);









        Intent data= getIntent();
        String Name= data.getStringExtra("Name");
        String CaloricIntake= data.getStringExtra("CaloricIntake");
        String Email= data.getStringExtra("Email");
        String GoalWeight= data.getStringExtra("GoalWeight");
        String Height= data.getStringExtra("Height");
        String Weight= data.getStringExtra("Weight");
        String GoalWeightKg= data.getStringExtra("WeightKgs");
        String GoalWeightPounds= data.getStringExtra("GoalWeightPounds");
        String HeightCm= data.getStringExtra("HeightCM");
        String HeightInches= data.getStringExtra("HeightInches");
        String WeightKgs= data.getStringExtra("WeightKgs");
        String WeightPounds= data.getStringExtra("WeightPounds");

        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        user= fAuth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();

        mName= findViewById(R.id.Name);
        mHeight= findViewById(R.id.Height);
        mWeight= findViewById(R.id.Weight);
        mEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        mSave= findViewById(R.id.buttonSave);
       // mLoginButton=findViewById(R.id.textView5);
        mCalories= findViewById(R.id.Calories);
        mGoalWeight=findViewById(R.id.GoalWeight);
        mProfileImage=findViewById(R.id.profileImageView);
        mToggleUnit= findViewById(R.id.toggleButtonUnit2);

        StorageReference profileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mProfileImage);
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });



        mToggleUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(mToggleUnit.isChecked()){



                    mName.setText(Name);
                    mEmailAddress.setText(Email);
                    mCalories.setText("fish");
                    mGoalWeight.setText(GoalWeightKg);
                    mHeight.setText(HeightInches);
                    mWeight.setText(Email);


                }

                if(!mToggleUnit.isChecked()){
                    mName.setText(Name);
                    mEmailAddress.setText(Email);
                    mCalories.setText(CaloricIntake);
                    mGoalWeight.setText(GoalWeightKg);
                    mHeight.setText(HeightCm);
                    mWeight.setText(WeightKgs);


                }
            }
        });



        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mName.getText().toString().isEmpty()||mEmailAddress.getText().toString().isEmpty()|| mHeight.getText().toString().isEmpty()
                       ||mWeight.getText().toString().isEmpty()||mCalories.getText().toString().isEmpty()||mGoalWeight.getText().toString().isEmpty()){
                   Toast.makeText(EditProfile.this,"One or Many Fields are Empty",Toast.LENGTH_SHORT).show();
                   return;
               }

               String email=mEmailAddress.getText().toString();
               user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       DocumentReference docRef= fStore.collection("users").document(user.getUid());
                       Map<String,Object> edited= new HashMap<>();
                       edited.put("Email",email);
                       edited.put("CaloricIntake",mCalories.getText().toString());
                       edited.put("Name",mName.getText().toString());
                       edited.put("GoalWeight",mGoalWeight.getText().toString());
                       edited.put("Height", mHeight.getText().toString());
                       edited.put("Weight", mWeight.getText().toString());
                       docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(EditProfile.this ,"Profile Updated", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(getApplicationContext(),MainActivity.class));
                               finish();
                           }
                       });



                      Toast.makeText(EditProfile.this,"Email is Changed",Toast.LENGTH_SHORT).show();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });

            }
        });

        mName.setText(Name);
        mEmailAddress.setText(Email);
        mCalories.setText(CaloricIntake);
        mGoalWeight.setText(GoalWeight);
        mHeight.setText(Height);
        mWeight.setText(Weight);

        Log.d(TAG,"onCreate: "+Name+" "+ CaloricIntake+" "+Email+" "+ GoalWeight+" "+Height);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode== Activity.RESULT_OK){
                Uri imageUri= data.getData();
                //mProfileImage.setImageURI(imageUri);


                uploadImageToFirebase(imageUri);

            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase storage
        StorageReference fileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();/////////////////////
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mProfileImage);
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}