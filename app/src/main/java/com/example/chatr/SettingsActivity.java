package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button saveChangesButton;
    private EditText username, status;
    private CircleImageView userProfilePicture;
    private ProgressDialog loadingBar;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference DBRef;
    private StorageReference ProfilePicRef;

    private static final int GalleryPic = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DBRef = FirebaseDatabase.getInstance().getReference();
        ProfilePicRef = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        InitializeComponents();

        //Listener to save user info
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUserInfo();
            }
        });

        GetUserInfo();

        //Allowing user to choose profile pic
        userProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPic);
            }
        });

    }



    //Initialize the components
    private void InitializeComponents() {
        saveChangesButton = (Button) findViewById(R.id.save_button);
        username = (EditText) findViewById(R.id.set_username);
        status = (EditText) findViewById(R.id.set_status);
        userProfilePicture = (CircleImageView) findViewById(R.id.profile_image);
        loadingBar = new ProgressDialog(this);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPic && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();

            //Image cropping utilized
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingBar.setTitle("Setting profile picture");
                loadingBar.setMessage("Your profile picture is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = ProfilePicRef.child(currentUserID+ ".jpg");

                //After cropping need to save image to storage unit
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, "Profile picture uploaded successfully...", Toast.LENGTH_SHORT).show();

                            //get image URL from storage
                            final String downloadedUrl = task.getResult().getStorage().toString();
                            System.out.println(downloadedUrl);
                            //pass URL to DB inside current user
                            DBRef.child("Users").child(currentUserID).child("image").setValue(downloadedUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(SettingsActivity.this, "Picture saved successfully...", Toast.LENGTH_SHORT).show();

                                        loadingBar.dismiss();

                                    }
                                    else{

                                        String errorMessage = task.getException().toString();
                                        Toast.makeText(SettingsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

                                        loadingBar.dismiss();

                                    }

                                }
                            });

                        }
                        else{
                            String errorMessage = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

                            loadingBar.dismiss();

                        }
                    }
                });
            }
        }

    }

    //Method to save user info
    private void SaveUserInfo() {

        String setUsername = username.getText().toString();
        String setStatus = status.getText().toString();

        if (TextUtils.isEmpty(setUsername)){
            Toast.makeText(this, "Please insert your user name first...", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUsername);
            profileMap.put("status",setStatus);

            DBRef.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SettingsActivity.this, "Profile updated successfully...", Toast.LENGTH_SHORT).show();
                        SendUserToMainActivity();
                    }
                    else{
                        String errorMessage = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error occurred: " +errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    //Checks the user info and returns the info from DB
    private void GetUserInfo() {

        DBRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){

                    String getUsername = dataSnapshot.child("name").getValue().toString();
                    String getStatus = dataSnapshot.child("status").getValue().toString();
                    String getProfilePicture = dataSnapshot.child("image").getValue().toString();

                    System.out.println(getProfilePicture);

                    username.setText(getUsername);
                    status.setText(getStatus);
                    Picasso.get().load(getProfilePicture).into(userProfilePicture);
                }
                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){

                    String getUsername = dataSnapshot.child("name").getValue().toString();
                    String getStatus = dataSnapshot.child("status").getValue().toString();

                    username.setText(getUsername);
                    status.setText(getStatus);


                }
                else{
                    Toast.makeText(SettingsActivity.this, "Please update your profile information...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Send user to main Activity
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
