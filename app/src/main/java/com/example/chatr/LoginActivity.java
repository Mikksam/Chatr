package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private Button loginButton, phoneLoginButton;
    private EditText usrEmail, usrPassword;
    private TextView forgotPasswordLink, createAccountLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        InitializeComponents();

        //Create-account-listener that activates on click and sends user to register Activity
        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        //sends user to the main activity if authentication is ok
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowLogin();
            }
        });
    }

    //Checks if user is allowed to log in and sends user to main activity if allowed
    private void AllowLogin() {

        String email = usrEmail.getText().toString();
        String password = usrPassword.getText().toString();

        //Check if fields are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT);
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT);
        }
        else{
            //If credentials are verified to be ok, send user to main activity
            loadingBar.setTitle("Signing in with your credentials!");
            loadingBar.setMessage("Please wait, while your account is being checked...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else{
                        String errorMessage = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error occurred: " + errorMessage, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    //Initialize components: Buttons, TextViews and EditText fields
    private void InitializeComponents() {

        loginButton = (Button) findViewById(R.id.login_button);
        phoneLoginButton = (Button) findViewById(R.id.phone_login_button);
        usrEmail = (EditText) findViewById(R.id.login_email);
        usrPassword = (EditText) findViewById(R.id.login_password);
        createAccountLink = (TextView) findViewById(R.id.create_account_login);
        forgotPasswordLink = (TextView) findViewById(R.id.forgot_password);
        loadingBar = new ProgressDialog(this);
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
