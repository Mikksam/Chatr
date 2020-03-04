package com.example.chatr;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    private View requestView;
    private RecyclerView mRequestList;
    private String currentUserID;

    private DatabaseReference RequestRef, UserRef, ContactsRef;
    private FirebaseAuth mAuth;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        requestView = inflater.inflate(R.layout.fragment_requests, container, false);

        //Initialize components
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RequestRef = FirebaseDatabase.getInstance().getReference().child("ChatRequests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        mRequestList = (RecyclerView) requestView.findViewById(R.id.pending_requests);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return requestView;
    }


    @Override
    public void onStart() {
        super.onStart();

        //Recycler options query
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(RequestRef.child(currentUserID),Contacts.class)
                .build();


        //Get all requests
        FirebaseRecyclerAdapter<Contacts,RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts contacts) {

                        holder.itemView.findViewById(R.id.accept_request_buton).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.decline_request_button).setVisibility(View.VISIBLE);

                        final String requested_user_id = getRef(position).getKey();

                        DatabaseReference GetTypeRef = getRef(position).child("request_type").getRef();

                        GetTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received")){

                                        UserRef.child(requested_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final String requestedProfilePicture = dataSnapshot.child("image").getValue().toString();

                                                    holder.profilePicture.setImageURI(requestedProfilePicture);

                                                }

                                                final String requestedUserName = dataSnapshot.child("name").getValue().toString();

                                                //To display status, use the variable below
                                                // final String requestedUserStatus = dataSnapshot.child("status").getValue().toString();

                                                holder.userName.setText(requestedUserName);
                                                holder.userStatus.setText("Wants To Connect With You");

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        CharSequence options[] = new CharSequence[]{
                                                                "Accept",
                                                                "Decline"
                                                        };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle("Chat Request from: " + requestedUserName);

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if (which == 0){

                                                                    ContactsRef.child(currentUserID).child(requested_user_id).child("Contacts")
                                                                            .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()){

                                                                                ContactsRef.child(requested_user_id).child(currentUserID).child("Contacts")
                                                                                        .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()){

                                                                                            RequestRef.child(currentUserID).child(requested_user_id)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                            if (task.isSuccessful()){

                                                                                                                RequestRef.child(requested_user_id).child(currentUserID)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                if (task.isSuccessful()){

                                                                                                                                    Toast.makeText(getContext(), "Request Denied", Toast.LENGTH_SHORT).show();
                                                                                                                                }

                                                                                                                            }
                                                                                                                        });
                                                                                                            }

                                                                                                        }
                                                                                                    });

                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                                if (which == 1){

                                                                    RequestRef.child(currentUserID).child(requested_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if (task.isSuccessful()){

                                                                                        RequestRef.child(requested_user_id).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if (task.isSuccessful()){

                                                                                                            Toast.makeText(getContext(), "Saved To Contacts", Toast.LENGTH_SHORT).show();
                                                                                                        }

                                                                                                    }
                                                                                                });
                                                                                    }

                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });

                                                        builder.show();
                                                    }

                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        RequestViewHolder viewHolder = new RequestViewHolder(view);
                        return viewHolder;
                    }
                };

        mRequestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;
        SimpleDraweeView profilePicture;
        Button acceptButton, declineButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_profile_status);
            profilePicture = itemView.findViewById(R.id.user_profile_picture);
            acceptButton = itemView.findViewById(R.id.accept_request_buton);
            declineButton = itemView.findViewById(R.id.decline_request_button);

        }
    }
}
