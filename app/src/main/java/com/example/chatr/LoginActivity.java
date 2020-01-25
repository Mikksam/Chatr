package com.example.chatr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private Button loginButton, phoneLoginButton;
    private EditText usrEmail, usrPassword;
    private TextView forgotPasswordLink, createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeComponents();

        //Create-account-listener that activates on click and sends user to register Activity
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
    }

    //Initialize components: Buttons, TextViews and EditText fields
    private void InitializeComponents() {

        loginButton = (Button) findViewById(R.id.login_button);
        phoneLoginButton = (Button) findViewById(R.id.phone_login_button);
        usrEmail = (EditText) findViewById(R.id.login_email);
        usrPassword = (EditText) findViewById(R.id.login_password);
        createAccount = (TextView) findViewById(R.id.create_account_login);
        forgotPasswordLink = (TextView) findViewById(R.id.forgot_password);
    }

    //Check if already logged in
    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser != null){
            SendUserToMainActivity();
        }

    }

    //Send user to main Activity
    private void SendUserToMainActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }

    //Send user to register Activity
    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}