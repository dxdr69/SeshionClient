package com.seshion.seshionclient;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class UserAccount implements Serializable {
    private String username;
    private String password;
    private double currentLatitude;
    private double currentLongitude;
    private String description;
    private boolean isOnline;
    private boolean isVisibilityPrivate;
    private List<UserAccount> friends;
    private List<UserAccount> friendRequests;
    private List<UserSession> ownedSessions;
    private List<UserSession> invitedSessions;
    private List<UserSession> joinedSessions;
    private List<UserGroup> ownedGroups;
    private List<UserGroup> joinedGroups;
    private List<Message> messages;
    private List<UserSession> allOpenSessions;



    UserAccount(String username)    {

        this.username = username;
        this.password = "";
        isOnline = true;
        isVisibilityPrivate = false;
        friends = new ArrayList<UserAccount>();
        ownedSessions = new ArrayList<UserSession>();
        invitedSessions = new ArrayList<UserSession>();
        joinedSessions = new ArrayList<UserSession>();
        ownedGroups = new ArrayList<UserGroup>();
        joinedGroups = new ArrayList<UserGroup>();
        messages = new ArrayList<Message>();
    }

    UserAccount(String username, String password)
    {
        this.username = username;
        this.password = password;
        isOnline = true;
        isVisibilityPrivate = false;
        friends = new ArrayList<UserAccount>();
        ownedSessions = new ArrayList<UserSession>();
        invitedSessions = new ArrayList<UserSession>();
        joinedSessions = new ArrayList<UserSession>();
        ownedGroups = new ArrayList<UserGroup>();
        joinedGroups = new ArrayList<UserGroup>();
        messages = new ArrayList<Message>();
    }

    UserAccount(String username, double currentLatitude, double currentLongitude,
                boolean isOnline, boolean isVisibilityPrivate, List<UserSession> joinedSessions,
                List<UserSession> ownedSessions)
    {
        this.username = username;
        password = null;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.isOnline = isOnline;
        this.isVisibilityPrivate = isVisibilityPrivate;
        friends = null;
        ownedSessions = null;
        invitedSessions = null;
        this.ownedSessions = ownedSessions;
        this.joinedSessions = joinedSessions;
        ownedGroups = null;
        joinedGroups = null;
        messages = null;
    }

    UserAccount(String username, boolean isOnline)
    {
        this.username = username;
        this.isOnline = isOnline;
    }

    public void checkIntoSeshion(UserSession seshion){
        this.joinedSessions.add(seshion);
    }

    public void checkOutOfSeshion(){
        this.joinedSessions.clear();
    }

    public void setUserName(String username)
    {
        this.username = username;
    }

    public String getUserName()
    {
        return username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setLatitude(double latitude)
    {
        currentLatitude = latitude;
    }

    public double getLatitude()
    {
        return currentLatitude;
    }

    public void setLongitude(double longitude)
    {
        currentLongitude = longitude;
    }

    public double getLongitude()
    {
        return currentLongitude;
    }

    public void setDescription(String desc)
    {
        description = desc;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isLoggedIn()
    {
        if (isOnline)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isProfilePrivate()
    {
        if (isVisibilityPrivate)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void changeLoggedInStatus()
    {
        if (!isOnline)
        {
            isOnline = true;
        }
        else
        {
            isOnline = false;
        }
    }

    public void changeProfileVisibility()
    {
        if (isVisibilityPrivate)
        {
            isVisibilityPrivate = false;
        }
        else
        {
            isVisibilityPrivate = true;
        }
    }

    public void addFriend(UserAccount friendUsername)
    {
        friends.add(friendUsername);
    }

    public void removeFriend(UserAccount friendUsername)
    {
        friends.remove(friendUsername);
    }

    public List<UserAccount> getAllFriends()
    {
        return friends;
    }

    public void setAllFriends(List<UserAccount> friends)
    {
        this.friends = friends;
    }

    public List<UserAccount> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<UserAccount> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void addOwnedSession(UserSession sesh)
    {
        ownedSessions.add(sesh);
    }

    public List<UserSession> getOwnedSessions()
    {
        return ownedSessions;
    }

    public void addInvitedSession(UserSession sesh)
    {
        invitedSessions.add(sesh);
    }

    public List<UserSession> getInvitedSessions()
    {
        return invitedSessions;
    }

    public void addJoinedSession(UserSession sesh)
    {
        joinedSessions.add(sesh);
    }

    public List<UserSession> getJoinedSessions()
    {
        return joinedSessions;
    }

    public void addOwnedGroup(UserGroup group)
    {
        ownedGroups.add(group);
    }

    public List<UserGroup> getOwnedGroups()
    {
        return ownedGroups;
    }

    public void addJoinedGroup(UserGroup group)
    {
        joinedGroups.add(group);
    }

    public List<UserGroup> getJoinedGroups()
    {
        return joinedGroups;
    }

    public void addMessage(Message msg)
    {
        messages.add(msg);
    }

    public List<Message> getAllMessages()
    {
        return messages;
    }

    public void setOwnedSessions(List<UserSession> ownedSessions) {
        this.ownedSessions = ownedSessions;
    }

    public void setInvitedSessions(List<UserSession> invitedSessions) {
        this.invitedSessions = invitedSessions;
    }

    public void setJoinedSessions(List<UserSession> joinedSessions) {
        this.joinedSessions = joinedSessions;
    }

    public void setOwnedGroups(List<UserGroup> ownedGroups) {
        this.ownedGroups = ownedGroups;
    }

    public void setJoinedGroups(List<UserGroup> joinedGroups) {
        this.joinedGroups = joinedGroups;
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