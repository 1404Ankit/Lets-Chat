package com.cdac.kp.androidchat.interfaces;

import java.io.UnsupportedEncodingException;

/**
 * Created by ANKUR on 20-07-2016.
 */
public interface Manager
{
    public String getUsername();
    public boolean sendMessage(String username, String toUsername, String message) throws UnsupportedEncodingException;
    public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException;
    public void messageReceived(String usrname, String message);

    public boolean isNetworkConnected();
    public boolean isUserAuthenticate();
    public String getLAstRawFriendList();
    public void exit();

    public String signUpUser(String usernameText, String passwordText, String email);
    public String addNewFriendRequest(String friendUsername);
    public String senFriendRequestResponse(String approvedFriendNames, String discardedFriendNames);
}
