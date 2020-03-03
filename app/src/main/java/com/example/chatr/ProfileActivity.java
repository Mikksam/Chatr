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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String receivedId, CurrentState, currentUser;

    private SimpleDraweeView profilePicture;
    private TextView userName, userStatus;
    private Button requestButton;

    private DatabaseReference UserRef, ChatRequestRef;
    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("ChatRequests");

        profilePicture = (SimpleDraweeView) findViewById(R.id.visited_profile_image);
        userName = (TextView) findViewById(R.id.visited_user_name);
        userStatus = (TextView) findViewById(R.id.visited_user_status);
        requestButton = (Button) findViewById(R.id.send_request_button);
        CurrentState = "new";
    }

    //Gets user info
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

                    ManageChatRequests();

                }
                else {

                    String getUserName = dataSnapshot.child("name").getValue().toString();
                    String getUserStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(getUserName);
                    userStatus.setText(getUserStatus);

                    ManageChatRequests();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Manage chat requests
    private void ManageChatRequests() {

        ChatRequestRef.child(currentUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receivedId)){

                            String request_type = dataSnapshot.child(receivedId).child("request_type").getValue().toString();

                            //If request has been sent to user, change button text to cancel request
                            if (request_type.equals("sent")){

                                CurrentState = "request_sent";
                                requestButton.setText("Cancel Request");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //To eliminate sending requests to oneself and
        //allowing new requests to users that have not been befriended yet
        if (!currentUser.equals(receivedId)){

            requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    requestButton.setEnabled(false);

                    //check if there is sent request or not
                    if (CurrentState.equals("new")){
                        SendChatRequest();
                    }
                    if (CurrentState.equals("request_sent")){
                        CancelChatRequest();
                    }
                }
            });
        }
        else{
            requestButton.setVisibility(View.INVISIBLE);
        }

    }

    //Create chat request to DB
    private void SendChatRequest() {

        ChatRequestRef.child(currentUser).child(receivedId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            ChatRequestRef.child(receivedId).child(currentUser)
                                    .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        requestButton.setEnabled(true);
                                        CurrentState = "request_sent";
                                        requestButton.setText("Cancel Request");

                                    }
                                }
                            });
                        }
                    }
                });

    }

    //Cancel chat request to DB
    private void CancelChatRequest() {

        ChatRequestRef.child(currentUser).child(receivedId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            ChatRequestRef.child(receivedId).child(currentUser)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                requestButton.setEnabled(true);
                                                CurrentState = "new";
                                                requestButton.setText("Send Message Request");

                                            }

                                        }
                                    });

                        }

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
