package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String receivedId;

    private SimpleDraweeView profilePicture;
    private TextView userName, userStatus;
    private Button requestButton;

    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receivedId = getIntent().getExtras().get("clicked_user_id").toString();

        mToolbar = (Toolbar) findViewById(R.id.visited_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //If wanted, could set the title to username
        getSupportActionBar().setTitle("Profile view");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToAddFriendsActivity();
                finish();
            }
        });

        InitializeComponents();

        GetUserInfo();
    }

    //initialize components
    private void InitializeComponents() {

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        profilePicture = (SimpleDraweeView) findViewById(R.id.visited_profile_image);
        userName = (TextView) findViewById(R.id.visited_user_name);
        userStatus = (TextView) findViewById(R.id.visited_user_status);
        requestButton = (Button) findViewById(R.id.send_request_button);
    }

    private void GetUserInfo() {

        System.out.println(receivedId);

        UserRef.child(receivedId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){

                    String getProfilePicture = dataSnapshot.child("image").getValue().toString();
                    String getUserName = dataSnapshot.child("name").getValue().toString();
                    String getUserStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(getUserName);
                    userStatus.setText(getUserStatus);
                    profilePicture.setImageURI(getProfilePicture);

                }
                else {

                    String getUserName = dataSnapshot.child("name").getValue().toString();
                    String getUserStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(getUserName);
                    userStatus.setText(getUserStatus);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //Send user to add friends activity
    private void SendUserToAddFriendsActivity() {
        Intent friendsIntent = new Intent(ProfileActivity.this, AddFriendsActivity.class);
        friendsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(friendsIntent);
        finish();
    }
}
