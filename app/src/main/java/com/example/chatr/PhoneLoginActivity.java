package com.example.chatr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference PhoneLogRef;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private EditText phoneNumber, verificationCode;
    private Button phoneNumberButton, verifyButton;
    private TextView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        PhoneLogRef = FirebaseDatabase.getInstance().getReference();

        InitializeComponents();


        //Phone number check and authorization with firebase
        // Documentation in -> https://firebase.google.com/docs/auth/android/phone-auth
        phoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if there is a phone number
                if(TextUtils.isEmpty(phoneNumber.getText())){
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number...", Toast.LENGTH_SHORT).show();
                }
                else{

                    //Make phone number fields invisible so that cannot send again
                    phoneNumberButton.setVisibility(View.INVISIBLE);
                    phoneNumber.setVisibility(View.INVISIBLE);

                    //Make verification fields visible
                    verificationCode.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.VISIBLE);

                   /*
                   //For some reason doesn't understand TimeUnit and explodes when imported java-library for it
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                    */
                }
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
