package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText messageInput;
    private ScrollView mScrollView;
    private TextView displayMessages;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, GroupNameRef, MessageKeyRef;

    private String groupChatName, currentUserID, currentUsername, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //Gets the passed group chat name from intent
        groupChatName = getIntent().getExtras().get("groupName").toString();

        //Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupChatName);

        InitializeComponents();

        GetUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageToDB();

                messageInput.setText("");
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

        UserRef.addValueEventListener(new ValueEventListener() {
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
        String messageKey = GroupNameRef.push().getKey();

        if (!TextUtils.isEmpty(message)){

            //Get and format date to wanted form
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM/dd/YYYY");
            currentDate = currentDateFormat.format(calForDate.getTime());

            //Get and format time to wanted form
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            //Using HashMap to save message, date and time to DB
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            MessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUsername);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            MessageKeyRef.updateChildren(messageInfoMap);

        }


    }


}
