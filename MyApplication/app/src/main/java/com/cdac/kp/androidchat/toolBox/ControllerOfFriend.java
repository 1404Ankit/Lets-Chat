package com.cdac.kp.androidchat.toolBox;

import com.cdac.kp.androidchat.type.InfoOfFriends;

/**
 * Created by ANKUR on 20-07-2016.
 */
public class ControllerOfFriend
{
    public static InfoOfFriends[] friendsInfo;
    public static InfoOfFriends[] unapprovedFriends;
    private static String activeFriend;


    public static void setFriendsInfo(InfoOfFriends[] friends)
    {
        ControllerOfFriend.friendsInfo = friends;
    }

    public static InfoOfFriends checkFriends(String username, String userKey)
    {
        InfoOfFriends result = null;
        if(friendsInfo != null)
        {
            for (int i=0; i<friendsInfo.length; i++)
            {
                if (friendsInfo[i].Username.equals(username) && friendsInfo[i].UserKey.equals(userKey))
                {
                    result = friendsInfo[i];
                    break;
                }
            }
        }
        return result;
    }

    public static InfoOfFriends getFriendsInfo(String username)
    {
        InfoOfFriends result = null;
        if(friendsInfo != null)
        {
            for (int i=0; i<friendsInfo.length; i++)
            {
                if (friendsInfo[i].Username.equals(username))
                {
                    result = friendsInfo[i];
                    break;
                }
            }
        }
        return result;
    }

    public static void setActiveFriend(String friendName){
        activeFriend = friendName;
    }

    public static String getActiveFriend()
    {
        return activeFriend;
    }
}
