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

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText usrEmail, usrPassword;
    private TextView alreadyHaveAnAccount;

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialize to firebase
        mAuth = FirebaseAuth.getInstance();

        InitializeComponents();

        //Sends user back to login activity
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        //ClickListener for creating a new account
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

    }

    //Create new account with email and pwd to firebase
    private void CreateNewAccount() {

        String email = usrEmail.getText().toString();
        String password = usrPassword.getText().toString();
        //Check if fields are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT);
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT);
        }
        //Create account and show messages depending on the result
        else{
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait, while your account is being created...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SendUserToLoginActivity();
                        Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else{
                        String errorMessage = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error occurred: " + errorMessage, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    //Initialize components: Button, EditTexts and TextView
    private void InitializeComponents() {

        registerButton = (Button) findViewById(R.id.create_account_button);
        usrEmail = (EditText) findViewById(R.id.register_email);
        usrPassword = (EditText) findViewById(R.id.register_password);
        alreadyHaveAnAccount = (TextView) findViewById(R.id.existing_user_link);

        loadingBar = new ProgressDialog(this);
    }

    //Method to send user to login activity
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

}


