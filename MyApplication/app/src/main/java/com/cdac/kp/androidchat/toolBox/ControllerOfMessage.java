package com.cdac.kp.androidchat.toolBox;

import com.cdac.kp.androidchat.type.InfoOfMessage;

/**
 * Created by ANKUR on 20-07-2016.
 */
public class ControllerOfMessage
{
    public static final String taken = "taken";

    public static InfoOfMessage[] infoOfMessages = null;

    public static void setMessageInfo(InfoOfMessage[] infoOfMessages)
    {
        InfoOfMessage.infoOfMessages = infoOfMessages;
    }

    public static InfoOfMessage checkMessage(String username) {
        InfoOfMessage result = null;
        for (int i = 0; i < infoOfMessages.length; i++)
        {
            result = infoOfMessages[i];
        }
        return result;

    }

    public static InfoOfMessage[] getMessages()
    {
        return infoOfMessages;
    }
}
