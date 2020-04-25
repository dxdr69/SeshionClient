package com.seshion.seshionclient;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class StateParcel implements Parcelable {

    private List<UserAccount> friends;



    private List<UserAccount> friendRequests;
    private List<UserSession> ownedSessions;
    private List<UserSession> invitedSessions;
    private List<UserSession> joinedSessions;
    private List<UserGroup> ownedGroups;
    private List<UserGroup> joinedGroups;
    private List<Message> messages;
    private List<UserSession> allOpenSessions;

    UserAccount loggedInUser;
    UserSession usersSeshion;

    StateParcel() {};

    public StateParcel(List<UserAccount> friends, List<UserGroup> ownedGroups, List<Message> messages, List<UserSession> allOpenSessions){


        this.friends = friends;
        this.ownedGroups = ownedGroups;
        this.messages = messages;
        this.allOpenSessions = allOpenSessions;

    }

//    public StateParcel(ArrayList<String> friends, ArrayList<UserAccount> friendsList){
//
//        this.friendsListUserAccounts = friendsList;
//        this.friends = friends;
//
//    }

    protected StateParcel(Parcel in) {
        /* I guess you are supposed to put the objects in order here */
        //this.user = in.create
        //friends = in.createStringArrayList();
        in.readList(friends, UserAccount.class.getClassLoader());
        in.readList(ownedGroups, UserGroup.class.getClassLoader());
        in.readList(messages, Message.class.getClassLoader());
        in.readList(allOpenSessions, UserSession.class.getClassLoader());

        //in.readList(friendList, UserAccount.class.getClassLoader());

    }

    public static final Creator<StateParcel> CREATOR = new Creator<StateParcel>() {
        @Override
        public StateParcel createFromParcel(Parcel in) {
            return new StateParcel(in);
        }

        @Override
        public StateParcel[] newArray(int size) {
            return new StateParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        /* I guess you are supposed to put the objects in order here */
        //parcel.writeList(friendList);
        //parcel.writeValue(user);
        parcel.writeList(friends);
        parcel.writeList(ownedSessions);
    }



    public List<UserAccount> getFriends() {
        return friends;
    }

    public void setFriends(List<UserAccount> friends) {
        this.friends = friends;
    }

    public List<UserAccount> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<UserAccount> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<UserSession> getInvitedSessions() {
        return invitedSessions;
    }

    public void setInvitedSessions(List<UserSession> invitedSessions) {
        this.invitedSessions = invitedSessions;
    }

    public List<UserSession> getJoinedSessions() {
        return joinedSessions;
    }

    public void setJoinedSessions(List<UserSession> joinedSessions) {
        this.joinedSessions = joinedSessions;
    }

    public List<UserGroup> getOwnedGroups() {
        return ownedGroups;
    }

    public void setOwnedGroups(List<UserGroup> ownedGroups) {
        this.ownedGroups = ownedGroups;
    }

    public List<UserGroup> getJoinedGroups() {
        return joinedGroups;
    }

    public void setJoinedGroups(List<UserGroup> joinedGroups) {
        this.joinedGroups = joinedGroups;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<UserSession> getAllOpenSessions() {
        return allOpenSessions;
    }

    public void setAllOpenSessions(List<UserSession> allOpenSessions) {
        this.allOpenSessions = allOpenSessions;
    }

}
