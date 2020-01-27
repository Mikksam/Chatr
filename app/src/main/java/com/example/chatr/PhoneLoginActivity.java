package com.example.chatr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference PhoneLogRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        PhoneLogRef = FirebaseDatabase.getInstance().getReference();
    }


}
