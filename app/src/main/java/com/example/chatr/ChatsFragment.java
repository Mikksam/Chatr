package com.example.chatr;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View PrivateChatView;
    private RecyclerView chatList;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       PrivateChatView = inflater.inflate(R.layout.fragment_chats, container, false);

       chatList = (RecyclerView) PrivateChatView.findViewById(R.id.chat_list);
       chatList.setLayoutManager(new LinearLayoutManager(getContext()));

       //TODO !!! functionality !!!

       return PrivateChatView;

    }

}
