package com.cdac.kp.androidchat.com.cdac.kp.androidchat.serve;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cdac.kp.androidchat.ListOfFriends;
import com.cdac.kp.androidchat.LogginIn;
import com.cdac.kp.androidchat.com.cdac.kp.android.comm.Socketer;
import com.cdac.kp.androidchat.interfaces.Manager;
import com.cdac.kp.androidchat.interfaces.Updater;
import com.cdac.kp.androidchat.toolBox.ControllerOfFriend;
import com.cdac.kp.androidchat.toolBox.ParserXML;
import com.cdac.kp.androidchat.type.InfoOfFriends;
import com.cdac.kp.androidchat.type.InfoOfFriends;
import com.cdac.kp.androidchat.type.InfoOfMessage;



import org.dmc.cdac.myapplication.R;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by ANKUR on 26-07-2016.
 */
public class MessagingService<InfoOfFriends> extends Service implements Manager, Updater
{

    public static final String TAKE_MESSAGE = "Take_Message";
    public static final String FRIEND_LIST_UPDATED = "Take Friend List";
    public static final String MESSAGE_LIST_UPDATE = "Take Message List";
    public static String USERNAME;
    public ConnectivityManager conManager = null;
    private final int UPDATE_TIME_PERIOD = 15000;

    private String rawFriendList = new String();
    private String rawMessageList = new String();

    Socketer socketerOperator = new Socketer();

    private final IBinder mBinder = new IMBinder();
    private String username;
    private String password;
    private String userKey;
    private boolean authenticatedUser = false;
    private Timer timer;

    private NotificationManager mNM;

    public class IMBinder extends Binder {
        public Manager getService()
        {
            return MessagingService.this;
        }

    }

    @Override
    public void onCreate()
    {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        timer = new Timer();

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                Random random = new Random();
                int tryCount = 0;
                while (socketerOperator.startListeningPort(10000 + random.nextInt(20000))  == 0 )
                {
                    tryCount++;
                    if (tryCount > 10)
                    {
                        break;
                    }
                }
            }
        };
        thread.start();
    }



    @Override
    public boolean sendMessage(String username, String toUsername, String message) throws UnsupportedEncodingException
    {
        return true;
    }



    @Override
    public void messageReceived(String usrname, String message)
    {

    }

    @Override
    public boolean isNetworkConnected()
    {
        return conManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean isUserAuthenticate()
    {
        return authenticatedUser;
    }

    @Override
    public String getLAstRawFriendList()
    {
        return this.rawFriendList;
    }

    @Override
    public void onDestroy()
    {
        Log.i("IMService is being destroyed", "...");
        super.onDestroy();
    }

    @Override
    public void exit()
    {
        timer.cancel();
        socketerOperator.exit();
        socketerOperator = null;
        this.stopSelf();
    }

    @Override
    public String signUpUser(String usernameText, String passwordText, String email)
    {
        String params = "username=" + usernameText +
                "&password=" + passwordText +
                "&action=" + "signUpUser"+
                "&email=" + email+
                "&";

        String result = socketerOperator.sendHttpRequest(params);

        return result;
    }

    @Override
    public String addNewFriendRequest(String friendUsername)
    {
        String params = "username=" + this.username +
                "&password=" + this.password +
                "&action=" + "addNewFriend" +
                "&friendUserName=" + friendUsername +
                "&";

        String result = socketerOperator.sendHttpRequest(params);

        return result;
    }

    @Override
    public String senFriendRequestResponse(String approvedFriendNames, String discardedFriendNames)
    {
        String params = "username=" + this.username +
                "&password=" + this.password +
                "&action=" + "responseOfFriendReqs"+
                "&approvedFriends=" + approvedFriendNames +
                "&discardedFriends=" +discardedFriendNames +
                "&";

        String result = socketerOperator.sendHttpRequest(params);

        return result;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void updateDate(InfoOfMessage[] message, com.cdac.kp.androidchat.type.InfoOfFriends[] friends, com.cdac.kp.androidchat.type.InfoOfFriends[] unApprovedFriends, String userKey)
    {
        this.setUserKey(userKey);
        ControllerOfFriend.setFriendsInfo(friends);

    }

    public void showNotification(String username, String msg)
    {
       String title = username + ": " + ((msg.length() < 5) ? msg : msg.substring(0, 5)+ "...");
        Notification notification = new Notification(R.drawable.stat_sample,title,System.currentTimeMillis());

        Intent i = new Intent(this, MessagingService.class);
        i.putExtra(com.cdac.kp.androidchat.type.InfoOfFriends.Username, username);
        i.putExtra(com.cdac.kp.androidchat.type.InfoOfFriends.Message, msg);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

     //   notification.setLatestEventInfo(this, "New Message From " + username, msg, contentIntent);

        mNM.notify((username+msg).hashCode(), notification);
    }

    public String getUsername()
    {
        return username;
    }

    public boolean sendMessage(String username, String message)
    {
        com.cdac.kp.androidchat.type.InfoOfFriends friendInfo = ControllerOfFriend.getFriendsInfo(username);
        String IP = friendInfo.ip;

        int port = Integer.parseInt(friendInfo.port);

        String msg = friendInfo.Username + "=" + URLEncoder.encode(this.username) +
                "&" + friendInfo.UserKey + "=" + URLEncoder.encode(userKey) +
                "&" + friendInfo.Message + "=" + URLEncoder.encode(message) +
                "&" ;
        return socketerOperator.sendMessage(msg, IP, port);
    }

    private String getFriendList()
    {
        rawFriendList = socketerOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
        if (rawFriendList != null)
        {
            this.parsefriendInfo(rawFriendList);
        }
        return rawFriendList;
    }

    private void parsefriendInfo(String xml)
    {
        try {
            SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
            sp.parse(new ByteArrayInputStream(xml.getBytes()), new ParserXML(MessagingService.this));
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public String authenticateUser(String usernameText, String passwordText)
    {
        this.username = usernameText;
        this.password = passwordText;

        this.authenticatedUser = false;

        String result = this.getFriendList();

        if (result != null && !result.equals(LogginIn.AUTHENTICATION_FAILED))
        {
            this.authenticatedUser = true;
            rawFriendList = result;

            Intent i = new Intent(FRIEND_LIST_UPDATED);
            i.putExtra(com.cdac.kp.androidchat.type.InfoOfFriends.Friend_list, rawFriendList);
            sendBroadcast(i);

            timer.schedule(new TimerTask()
            {
                public void run()
                {
                    try {
                        Intent i = new Intent(FRIEND_LIST_UPDATED);
                        String tmp = MessagingService.this.getFriendList();
                        if (tmp != null)
                        {
                            i.putExtra(com.cdac.kp.androidchat.type.InfoOfFriends.Friend_list, tmp);
                            sendBroadcast(i);
                            Log.i("friend list broadcast sent ", "");
                        }
                        else {
                            Log.i("friend list returned null", "");
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    }
                }, UPDATE_TIME_PERIOD, UPDATE_TIME_PERIOD);
            }
        return result;
    }


    public void messageReceived(String message) {
        String[] params = message.split("&");
        String username = new String();
        String userKey = new String();
        String msg = new String();
        for (int i = 0; i < params.length; i++) {
            String[] localpar = params[i].split("=");
            if (localpar[0].equals(com.cdac.kp.androidchat.type.InfoOfFriends.Username)) {
                username = URLDecoder.decode(localpar[1]);
            } else if (localpar[0].equals(com.cdac.kp.androidchat.type.InfoOfFriends.UserKey)) {
                userKey = URLDecoder.decode(localpar[1]);
            } else if (localpar[0].equals(com.cdac.kp.androidchat.type.InfoOfFriends.Message)) {
                msg = URLDecoder.decode(localpar[1]);
            }
        }
        Log.i("Message received in service", message);


        com.cdac.kp.androidchat.type.InfoOfFriends friend = ControllerOfFriend.checkFriends(username, userKey);
        if (friend != null) {
            Intent i = new Intent(TAKE_MESSAGE);

            i.putExtra(com.cdac.kp.androidchat.type.InfoOfFriends.Username, friend.userName);
            i.putExtra(com.cdac.kp.androidchat.type.InfoOfFriends.Message, msg);
            sendBroadcast(i);
            String activeFriend = ControllerOfFriend.getActiveFriend();
            if (activeFriend == null || activeFriend.equals(username) == false) {
                showNotification(username, msg);
            }
            Log.i("TAKE_MESSAGE broadcast sent by im service", "");
        }
    }


    private String getAuthenticateUserParams(String usernameText, String passwordText)
    {
        String params = "username=" + URLEncoder.encode(usernameText) +
                "&password="+ URLEncoder.encode(passwordText) +
                "&action="  + URLEncoder.encode("authenticateUser")+
                "&port="    + URLEncoder.encode(Integer.toString(socketerOperator.getListeningPort())) +
                "&";

        return params;
    }

    public void setUserKey(String value)
    {
        this.userKey = value;
    }


}
