package com.example.chatr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccess extends FragmentPagerAdapter {

    public TabAccess(@NonNull FragmentManager fm) {
        super(fm);
    }

    //Creating access for tab-fragments

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 1:
                GroupChatsFragment groupChatsFragment = new GroupChatsFragment();
                return groupChatsFragment;

            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;

            case 3:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;


            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 4;
    }


    //Page titles for tabs
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            case 3:
                return "Requests";

            default:
                return null;
        }

    }
}
