package com.example.chatr;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {


    private View ContactView;
    private RecyclerView mContactList;
    private String currentUserID;

    private DatabaseReference ContactRef, UsersRef;
    private FirebaseAuth mAuth;



    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactView = inflater.inflate(R.layout.fragment_contacts, container, false);

        mContactList = (RecyclerView) ContactView.findViewById(R.id.contact_list);
        mContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return ContactView;
    }

    @Override
    public void onStart() {

        super.onStart();

        //Query connection
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContactRef, Contacts.class)
                .build();

        //Utilizing firebase adapter
        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts contacts) {

                String userID = getRef(position).getKey();

                UsersRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")){

                            String profilePicture = dataSnapshot.child("image").getValue().toString();
                            String userName = dataSnapshot.child("name").getValue().toString();
                            String userStatus = dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(userName);
                            holder.userStatus.setText(userStatus);
                            holder.profilePicture.setImageURI(profilePicture);

                        }
                        else{

                            String userName = dataSnapshot.child("name").getValue().toString();
                            String userStatus = dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(userName);
                            holder.userStatus.setText(userStatus);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;

            }
        };

        mContactList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        SimpleDraweeView profilePicture;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_profile_status);
            profilePicture = itemView.findViewById(R.id.user_profile_picture);

        }
    }

}
