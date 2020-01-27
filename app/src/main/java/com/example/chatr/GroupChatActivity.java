package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText messageInput;
    private ScrollView mScrollView;
    private TextView displayMessages;

    private FirebaseAuth mAuth;
    private DatabaseReference DBRef;

    private String groupChatName, currentUserID, currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DBRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //Gets the passed group chat name from intent
        groupChatName = getIntent().getExtras().get("groupName").toString();

        InitializeComponents();

        GetUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageToDB();
            }
        });

    }

    //Initialize components used in the view
    private void InitializeComponents() {

        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(groupChatName);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        messageInput = (EditText) findViewById(R.id.group_write_message);
        mScrollView = (ScrollView) findViewById(R.id.group_scroll_view);
        displayMessages = (TextView) findViewById(R.id.group_chat_display);
    }

    //Method to fetch user info from DB
    private void GetUserInfo() {

        DBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    currentUsername = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Method to send message to database
    private void SendMessageToDB() {

        String message = messageInput.getText().toString();


    }


}
