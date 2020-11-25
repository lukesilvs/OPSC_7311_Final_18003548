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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    TextView mLoginButton, mHeightNumber, mCurrentWeight, mGoalNumber;
    FirebaseFirestore fStore;
    String UserID;

    ToggleButton mToggleUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mName= findViewById(R.id.Name);
        //mHeight= findViewById(R.id.Height);
        //mWeight= findViewById(R.id.Weight);
        mEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        mPassword= findViewById(R.id.editTextTextPassword2);
        mRegister= findViewById(R.id.buttonRegister);
        mLoginButton=findViewById(R.id.textView5);
        mCalories= findViewById(R.id.Calories);
       // mGoalWeight=findViewById(R.id.GoalWeight);

        mGoalNumber= findViewById(R.id.GoalWeightNumber);
        mCurrentWeight=findViewById(R.id.CurrentWeight);
        mHeightNumber= findViewById(R.id.HeightNumber);
        mToggleUnit=findViewById(R.id.toggleUnit);//new




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
                String Height= mHeightNumber.getText().toString(); //probably have to store as int
                String Weight= mCurrentWeight.getText().toString();
                String Calories=mCalories.getText().toString();
                String GoalWeight= mGoalNumber.getText().toString();
                //new
                Double WeightKgs=1.1;
                Double WeightPounds=1.1;
                Double HeightCms=1.1;
                Double HeightInches=1.1;
                Double GoalWeightKgs=1.1;
                Double GoalWeightPounds=1.1;
                //new
                if(mToggleUnit.isChecked()){

                    WeightPounds=Double.parseDouble(Weight);
                    HeightInches=Double.parseDouble(Height);
                    GoalWeightPounds=Double.parseDouble(GoalWeight);
                    HeightCms=(HeightInches*2.5);
                    WeightKgs= (WeightPounds/2.2);
                    GoalWeightKgs=(GoalWeightPounds/2.2);

                }

                if(!mToggleUnit.isChecked()){
                    WeightKgs=Double.parseDouble(Weight);
                    HeightCms=Double.parseDouble(Height);
                    GoalWeightKgs=Double.parseDouble(GoalWeight);
                    HeightInches=(HeightCms/2.5);
                    WeightPounds= (WeightKgs*2.2);
                    GoalWeightPounds=(GoalWeightKgs*2.2);

                }


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
                Integer finalHeightCms = Integer.valueOf(HeightCms.intValue());
                Integer finalHeightInches = Integer.valueOf(HeightInches.intValue());
                Integer finalWeightKgs = Integer.valueOf(WeightKgs.intValue());
                Integer finalWeightPounds = Integer.valueOf(WeightPounds.intValue());
                Integer finalGoalWeightKgs= Integer.valueOf(GoalWeightKgs.intValue());
                Integer finalGoalWeightPounds= Integer.valueOf(GoalWeightPounds.intValue());

                String finalHeightCmsString = finalHeightCms.toString();
                String finalHeightInchesString = finalHeightInches.toString();
                String finalWeightKgsString = finalWeightKgs.toString();
                String finalWeightPoundsString = finalWeightPounds.toString();
                String finalGoalWeightKgsString= finalGoalWeightKgs.toString();
                String finalGoalWeightPoundsString= finalGoalWeightPounds.toString();
                String finalHeight=Height.toString();

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"User Created", Toast.LENGTH_SHORT).show();
                            UserID= fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fStore.collection("users").document(UserID);
                            Map<String,Object> user= new HashMap<>();
                            //new
                            user.put("Name",Name);
                            user.put("Height",finalHeight);
                            user.put("HeightCM", finalHeightCmsString);
                            user.put("HeightInches", finalHeightInchesString);
                            user.put("Weight", Weight);
                            user.put("WeightKgs", finalWeightKgsString);
                            user.put("WeightPounds", finalWeightPoundsString);
                            user.put("Email", email);
                            user.put("GoalWeight", GoalWeight);
                            user.put("GoalWeightKgs", finalGoalWeightKgsString);
                            user.put("GoalWeightPounds", finalGoalWeightPoundsString);
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