package com.cdac.kp.androidchat.interfaces;

import com.cdac.kp.androidchat.type.InfoOfFriends;
import com.cdac.kp.androidchat.type.InfoOfMessage;

/**
 * Created by ANKUR on 20-07-2016.
 */
public interface Updater
{
    public void updateDate(InfoOfMessage[] message, InfoOfFriends[] friends, InfoOfFriends[] unApprovedFriends, String userKey);
}
