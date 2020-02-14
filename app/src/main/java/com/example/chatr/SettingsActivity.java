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

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;


public class SettingsActivity extends AppCompatActivity {

    private Button saveChangesButton;
    private EditText username, status;
    private SimpleDraweeView userProfilePicture;
    private ProgressDialog loadingBar;
    private Uri picLink;
    private Uri uri;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference DBRef;
    private StorageReference ProfilePicRef;

    private static final int GalleryPic = 1;

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DBRef = FirebaseDatabase.getInstance().getReference();
        ProfilePicRef = FirebaseStorage.getInstance().getReference();

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
        userProfilePicture = (SimpleDraweeView) findViewById(R.id.my_profile_image);
        loadingBar = new ProgressDialog(this);
    }

    //select and save profile pic
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

                uri = result.getUri();

                SavePictureToFirebase();

                loadingBar.dismiss();
            }
        }

    }

    private void SavePictureToFirebase() {

        final DatabaseReference reference = DBRef;
        final StorageReference imageRef = ProfilePicRef.child("ProfilePictures").child(currentUserID+ ".jpg");

        //After cropping need to save image to storage unit
        UploadTask uploadTask = imageRef.putFile(uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "Error occurred while trying to save picture: " +e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageMetadata snapshotMetadata = taskSnapshot.getMetadata();
                final Task<Uri> downloadUrl = imageRef.getDownloadUrl();

                downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String imageReference = uri.toString();

                        //To chech the url
                        System.out.println(imageReference);
                        DatabaseReference ImgRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("image");
                        ImgRef.setValue(imageReference);




                        Toast.makeText(SettingsActivity.this, "Profile picture updated successfully!!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });

            }
        });

    }

    //Method to save user info
    private void SaveUserInfo() {

        String setUsername = username.getText().toString();
        String setStatus = status.getText().toString();

        String picUri = user.getProfilePic();

        if (TextUtils.isEmpty(setUsername)){
            Toast.makeText(this, "Please insert your user name first...", Toast.LENGTH_SHORT).show();
        }
        else{

            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUsername);
            profileMap.put("status",setStatus);
            profileMap.put("image", picUri);


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

                    username.setText(getUsername);
                    status.setText(getStatus);
                    //picLink = Uri.parse("https://static.cardmarket.com/img/b37bab18388963d54674993e0b454c1d/items/1/ELD/398659.jpg");
                    //picLink = Uri.parse(getProfilePicture);

                    //Retrieve the picture and show it (Fresco does it automatically)

                    userProfilePicture.setImageURI(getProfilePicture);

                    //Set values for user class for handling the data
                    user.setCurrentUser(getUsername);
                    user.setStatus(getStatus);
                    user.setProfilePic(getProfilePicture);

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
