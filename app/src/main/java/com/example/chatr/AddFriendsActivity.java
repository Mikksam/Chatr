package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView friendsList;

    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsList = (RecyclerView) findViewById(R.id.add_friends_list);

        mToolbar = (Toolbar) findViewById(R.id.add_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Friend");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMainActivity();
                finish();
            }
        });

    }

    //Utilize Firebase recycler adapter
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UsersRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options){
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {

                        holder.username.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                        holder.profilePicture.setImageURI(model.getImage());

                        //to get the UID for a person to add
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String clicked_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(AddFriendsActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("clicked_user_id",clicked_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };

        //Create a manager for layout (needed this to display users)
        //Also set the manager and adapter for recycler view
        LinearLayoutManager mLinearManager = new LinearLayoutManager(this);
        mLinearManager.setOrientation(LinearLayoutManager.VERTICAL);
        friendsList.setLayoutManager(mLinearManager);
        friendsList.setAdapter(adapter);
        adapter.startListening();

    }

    //ViewHolder class for displaying all users
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{

        TextView username, userStatus;
        SimpleDraweeView profilePicture;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_profile_status);
            profilePicture = itemView.findViewById(R.id.user_profile_picture);

        }
    }

    //Send user to main Activity
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(AddFriendsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
