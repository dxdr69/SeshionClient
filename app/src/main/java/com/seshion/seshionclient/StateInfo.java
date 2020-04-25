package com.seshion.seshionclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Just a simple model object to hold the current state of the program including:
 * The currently logged in user's account object
 * His/her friend stuff,
 * His/her sesion stuff,
 * His/her messages
 */
public class StateInfo {

    /* state vars */
    private String loginResponse;
    private UserAccount userAccount;
    private List<UserAccount> usersFriends;
    private List<UserGroup> ownedFriendGroups, joinedFriendGroups;
    private List<Message> messages;
    private List<UserSession> ownedSeshions, invitedSeshions, joinedSeshions, allOpenSeshions;

    /* empty constructor, everything done with getters and setters for simplicity */
    public StateInfo() {}

    /* getters and setters */
    public String getLoginResponse() {
        return loginResponse;
    }
    public void setLoginResponse(String loginResponse) {
        this.loginResponse = loginResponse;
    }

    public List<UserAccount> getUsersFriends() {
        return usersFriends;
    }
    public void setUsersFriends(List<UserAccount> usersFriends) { this.usersFriends = usersFriends; }

    public UserAccount getUserAccount() {
        return userAccount;
    }
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public List<UserGroup> getOwnedFriendGroups() {
        return ownedFriendGroups;
    }
    public void setOwnedFriendGroups(List<UserGroup> ownedFriendGroups) {
        this.ownedFriendGroups = ownedFriendGroups;
    }

    public List<UserGroup> getJoinedFriendGroups() {
        return joinedFriendGroups;
    }
    public void setJoinedFriendGroups(List<UserGroup> joinedFriendGroups) {
        this.joinedFriendGroups = joinedFriendGroups;
    }

    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<UserSession> getOwnedSeshions() {
        return ownedSeshions;
    }
    public void setOwnedSeshions(List<UserSession> ownedSeshions) {
        this.ownedSeshions = ownedSeshions;
    }

    public List<UserSession> getInvitedSeshions() {
        return invitedSeshions;
    }
    public void setInvitedSeshions(List<UserSession> invitedSeshions) {
        this.invitedSeshions = invitedSeshions;
    }

    public List<UserSession> getJoinedSeshions() {
        return joinedSeshions;
    }
    public void setJoinedSeshions(List<UserSession> joinedSeshions) {
        this.joinedSeshions = joinedSeshions;
    }

    public List<UserSession> getAllOpenSeshions() {
        return allOpenSeshions;
    }
    public void setAllOpenSeshions(List<UserSession> allOpenSeshions) {
        this.allOpenSeshions = allOpenSeshions;
    }

}
