package com.example.chatr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference PhoneLogRef;

    private EditText phoneNumber, verificationCode;
    private Button phoneNumberButton, verifyButton;
    private TextView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        PhoneLogRef = FirebaseDatabase.getInstance().getReference();

        InitializeComponents();

        //Sends phone number to Firebase
        phoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Calls for method that sends user back to login activity
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

 }


    //Initialize components used in the phone login layout
    private void InitializeComponents() {

        phoneNumber = (EditText) findViewById(R.id.phone_number);
        verificationCode = (EditText) findViewById(R.id.phone_verification_number);
        phoneNumberButton = (Button) findViewById(R.id.phone_submit_button);
        verifyButton = (Button) findViewById(R.id.verify_button);
        goBack = (TextView) findViewById(R.id.go_back_link);

    }

    //Send user to login activity
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }


}
