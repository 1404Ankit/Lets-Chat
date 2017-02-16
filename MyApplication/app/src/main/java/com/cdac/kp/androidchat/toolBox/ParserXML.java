package com.cdac.kp.androidchat.toolBox;

import android.util.Log;

import com.cdac.kp.androidchat.interfaces.Updater;
import com.cdac.kp.androidchat.type.InfoOfFriends;
import com.cdac.kp.androidchat.type.InfoOfMessage;
import com.cdac.kp.androidchat.type.InfoStatus;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Vector;

/**
 * Created by ANKUR on 20-07-2016.
 */
public class ParserXML extends DefaultHandler
{
    public String userKey = new String();
    public Updater updater;

    public ParserXML(Updater updater)
    {
        this.updater = updater;
    }

    private Vector<InfoOfFriends> mFriends = new Vector<InfoOfFriends>();
    private Vector<InfoOfFriends> mOnlineFriends = new Vector<InfoOfFriends>();
    private Vector<InfoOfFriends> mUnapprovedFriends = new Vector<InfoOfFriends>();

    private Vector<Updater> mUnreadMessages = new Vector<Updater>();


    public void endDocument() throws SAXException
    {
        InfoOfFriends[] friends = new InfoOfFriends[mFriends.size() + mOnlineFriends.size()];
        Updater[] messages = new Updater[mUnreadMessages.size()];

        int onLineFriendCount = mOnlineFriends.size();
        for (int i=0; i<onLineFriendCount; i++)
        {
            friends[i] = mOnlineFriends.get(i);
        }

        int offLineFriendCount = mFriends.size();
        for (int i=0; i<offLineFriendCount; i++)
        {
            friends[i + onLineFriendCount] = mFriends.get(i);
        }

        int unApprovedFrientCount = mUnapprovedFriends.size();
        InfoOfFriends[] unApprovedFriends = new InfoOfFriends[unApprovedFrientCount];
        for (int i=0; i<unApprovedFriends.length; i++)
        {
            friends[i] = mUnapprovedFriends.get(i);
        }

        int unReadMessageCount = mUnreadMessages.size();
        for (int i=0; i<unReadMessageCount; i++)
        {
            messages[i] = mUnreadMessages.get(i);
            Log.i("Message Log","i="+i);
        }
        this.updater.updateDate((InfoOfMessage[]) messages, friends, unApprovedFriends, userKey);
        super.endDocument();
    }

    public void startElements(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        if(localName == "friend")
        {
            InfoOfFriends friend = new InfoOfFriends();
            friend.userName = attributes.getValue(InfoOfFriends.Username);
            String status = attributes.getValue(InfoOfFriends.Status);
            friend.port = attributes.getValue(InfoOfFriends.Port);

            if (status != null && status.equals("online"))
            {
                friend.status = InfoStatus.OFFLINE;
                mOnlineFriends.add(friend);
            }
            else if (status.equals("unApproved"))
            {
                friend.status = InfoStatus.UNAPPROVED;
                mUnapprovedFriends.add(friend);
            }
            else
            {
                friend.status = InfoStatus.OFFLINE;
                mFriends.add(friend);
            }
        }
        else if (localName == "user")
        {
            this.userKey = attributes.getValue(InfoOfFriends.UserKey);
        }
        super.startElement(uri, localName, name, attributes);
    }


    @Override
    public void startDocument() throws SAXException
    {
        this.mFriends.clear();
        this.mOnlineFriends.clear();
        this.mUnreadMessages.clear();
        super.startDocument();
    }
}
