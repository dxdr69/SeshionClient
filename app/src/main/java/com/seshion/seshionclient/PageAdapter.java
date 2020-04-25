package com.seshion.seshionclient;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;


    private List<UserAccount> friends;
    private List<UserAccount> friendRequests;
    private List<UserSession> ownedSessions;
    private List<UserSession> invitedSessions;
    private List<UserSession> joinedSessions;
    private List<UserGroup> ownedGroups;
    private List<UserGroup> joinedGroups;
    private List<Message> messages;
    private List<UserSession> allOpenSessions;
    private String PARCEL_KEY = "data";


    public PageAdapter(FragmentManager fm, int numberOfTabs,
                       List<UserAccount> friendList,
                       List<UserGroup> ownedGroups,
                       List<Message> messages,
                       List<UserSession> allOpenSessions ) {
        super(fm);
        this.numOfTabs = numberOfTabs;
        this.friends = friendList;
        this.ownedGroups = ownedGroups;
        this.messages = messages;
        this.allOpenSessions = allOpenSessions;

//        seshions.add(sesh);
//        seshions.add(sesh2);
//        seshions.add(sesh3);
//        seshions.add(sesh4);

    }

    @Override
    public Fragment getItem(int position) {

        /* create bundle for seshion maps */
        Bundle bundle = new Bundle();
        StateParcel parcel = new StateParcel(friends, ownedGroups, messages, allOpenSessions);
        bundle.putParcelable(PARCEL_KEY, parcel);

        switch (position) {
            case 0:
                System.out.println("returning position 0");
//                someStrings.add(sesh.getName());
//                someStrings.add(sesh2.getName());
//                someStrings.add(sesh3.getName());
//                someStrings.add(sesh4.getName());

//                users.add(user1);
//                users.add(user2);
//                users.add(user3);
//                users.add(user4);

                /* create bundle for friendzone */
//                Bundle friendBundle = new Bundle();
//                StateParcel friendParcel = new StateParcel(friends, ownedSessions);
//                friendBundle.putParcelable(PARCEL_KEY, friendParcel);

                /* create the friendzone activity and set args */
                FriendZone friendZone = new FriendZone();
                friendZone.setArguments(bundle);
                //return new FriendZone();
                return friendZone;
            case 1:
                System.out.println("returning position 1");

                /* create bundle for friendzone */
//                Bundle seshionBundle = new Bundle();
//                StateParcel stateParcel = new StateParcel(friends, ownedSessions);
//                seshionBundle.putParcelable(PARCEL_KEY, stateParcel);

                /* create the seshion activity and set args */
                Seshions seshions = new Seshions();
                seshions.setArguments(bundle);
                return seshions;

            //return new Seshions();
            case 2:
                System.out.println("returning position 2");

                /* create bundle for seshion maps */
//                Bundle mapsBundle = new Bundle();
//                StateParcel mapsParcel = new StateParcel(friends, ownedSessions);
//                mapsBundle.putParcelable(PARCEL_KEY, mapsParcel);

                /* create the seshionmap activity and set args */
                SeshionMap seshionMap = new SeshionMap();
                seshionMap.setArguments(bundle);
                return seshionMap;

            //return new SeshionMap();
            default:
                System.out.println("returning null from switch");
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
        //return super.getItemPosition(object);
    }

    public List<UserAccount> getFriends() {
        return friends;
    }

    public void setFriends(List<UserAccount> friends) {
        this.friends = friends;
    }
}
