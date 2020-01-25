package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button saveChangesButton;
    private EditText username, status;
    private CircleImageView userProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference DBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DBRef = FirebaseDatabase.getInstance().getReference();

        InitializeComponents();

        //Listener to save user info
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUserInfo();
            }
        });

        GetUserInfo();
    }



    //Initialize the components
    private void InitializeComponents() {
        saveChangesButton = (Button) findViewById(R.id.save_button);
        username = (EditText) findViewById(R.id.set_username);
        status = (EditText) findViewById(R.id.set_status);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_image);
    }

    //Method to save user info
    private void SaveUserInfo() {

        String setUsername = username.getText().toString();
        String setStatus = status.getText().toString();

        if (TextUtils.isEmpty(setUsername)){
            Toast.makeText(this, "Please insert your user name first...", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUsername);
            profileMap.put("status",setStatus);

            DBRef.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SettingsActivity.this, "Profile updated successfully...", Toast.LENGTH_SHORT).show();
                        SendUserToMainActivity();
                    }
                    else{
                        String errorMessage = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error occurred: " +errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    //Checks the user info and returns the info from DB
    private void GetUserInfo() {

        DBRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){

                    String getUsername = dataSnapshot.child("name").getValue().toString();
                    String getStatus = dataSnapshot.child("status").getValue().toString();
                    String getProfileImage = dataSnapshot.child("image").getValue().toString();

                    username.setText(getUsername);
                    status.setText(getStatus);
                    //userProfileImage.
                }
                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){

                    String getUsername = dataSnapshot.child("name").getValue().toString();
                    String getStatus = dataSnapshot.child("status").getValue().toString();

                    username.setText(getUsername);
                    status.setText(getStatus);

                }
                else{
                    Toast.makeText(SettingsActivity.this, "Please update your profile information...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //Send user to main Activity
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
