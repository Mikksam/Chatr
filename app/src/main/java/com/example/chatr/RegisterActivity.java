package com.example.chatr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText usrEmail, usrPassword;
    private TextView alreadyHaveAnAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitializeComponents();

        //Sends user back to login activity
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    //Initialize components: Button, EditTexts and TextView
    private void InitializeComponents() {

        registerButton = (Button) findViewById(R.id.create_account_button);
        usrEmail = (EditText) findViewById(R.id.register_email);
        usrPassword = (EditText) findViewById(R.id.register_password);
        alreadyHaveAnAccount = (TextView) findViewById(R.id.existing_user_link);
    }

    //Method to send user to login activity
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

}


