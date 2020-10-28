package com.ldsilver.task2final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mEmailAddress, mPassword, mName, mHeight, mWeight, mCalories, mGoalWeight;
    Button mRegister;
    FirebaseAuth fAuth;
    TextView mLoginButton;
    FirebaseFirestore fStore;
    String UserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mName= findViewById(R.id.Name);
        mHeight= findViewById(R.id.Height);
        mWeight= findViewById(R.id.Weight);
        mEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        mPassword= findViewById(R.id.editTextTextPassword2);
        mRegister= findViewById(R.id.buttonRegister);
        mLoginButton=findViewById(R.id.textView5);
        mCalories= findViewById(R.id.Calories);
        mGoalWeight=findViewById(R.id.GoalWeight);



        fAuth=FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser()!= null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= mEmailAddress.getText().toString().trim();
                String password= mPassword.getText().toString().trim();
                String Name=mName.getText().toString();
                String Height= mHeight.getText().toString(); //probably have to store as int
                String Weight= mWeight.getText().toString();
                String Calories=mCalories.getText().toString();
                String GoalWeight= mGoalWeight.getText().toString();


                //validate

                if(TextUtils.isEmpty((email))){
                    mEmailAddress.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty((password))){
                    mPassword.setError("Email is Required.");
                    return;
                }

                if(password.length()<6){
                    mPassword.setError("Password must be >= 6 Characters");
                    return;
                }

                //Register User in Firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"User Created", Toast.LENGTH_SHORT).show();
                            UserID= fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fStore.collection("users").document(UserID);
                            Map<String,Object> user= new HashMap<>();
                            user.put("Name",Name);
                            user.put("Height",Height);
                            user.put("Weight", Weight);
                            user.put("Email", email);
                            user.put("GoalWeight", GoalWeight);
                            user.put("CaloricIntake", Calories);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User Profile is created for "+ UserID);
                                }
                            });


                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            Toast.makeText(Register.this,"Error"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
}