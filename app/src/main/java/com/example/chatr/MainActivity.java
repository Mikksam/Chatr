package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.viewpager.widget.ViewPager;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabAccess mTabAccess;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference DBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Fresco for image handling
        Fresco.initialize(this);

        //Initialize Firebase methods
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        DBRef = FirebaseDatabase.getInstance().getReference();

        //Initialize toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chatr");

        //Set up tabs for main activity's menu bar
        mViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);

        mTabAccess = new TabAccess(getSupportFragmentManager());
        mViewPager.setAdapter(mTabAccess);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    //Check if user is logged in, if not, proceed to SendUserToLoginActivity method
    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        else{
            VerifyUserExists();
        }

    }

    //Method to check if user exists in DB then allows use without login
    //If user settings have not been modified yet, sends "new" user to settings activity
    private void VerifyUserExists() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        DBRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Add menu to layout on create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    //Create access to menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_logout_option){
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.menu_settings_option){
            SendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.menu_group_option){
            MakeNewGroup();
        }
        if (item.getItemId() == R.id.menu_add_friend_option){
            AddFriend();
        }

        return true;
    }


    //Creates a dialog for making a new group
    //Sends the created group name as a parameter to method that saves ti then to DB
    private void MakeNewGroup() {
        AlertDialog.Builder groupBuilder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        groupBuilder.setTitle("Enter name for the Group: ");

        final EditText enterGroupName = new EditText(MainActivity.this);
        enterGroupName.setHint("***My Group Name***");
        groupBuilder.setView(enterGroupName);

        groupBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = enterGroupName.getText().toString();

                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please enter Group Name...", Toast.LENGTH_LONG).show();
                }
                else{
                    CreateNewGroup(groupName);
                }
            }
        });

        groupBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        groupBuilder.show();
    }

    //Saves the created group to DB. Gets group name from the method above
    private void CreateNewGroup(String groupName) {
        DBRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Group created successfully!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Send user to login activity
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //Send user to settings activity
    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }

    //Sends user to add friends to contacts
    private void AddFriend() {

        Intent addFriendsIntent = new Intent(MainActivity.this, AddFriendsActivity.class);
        addFriendsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(addFriendsIntent);
        finish();

    }


}
