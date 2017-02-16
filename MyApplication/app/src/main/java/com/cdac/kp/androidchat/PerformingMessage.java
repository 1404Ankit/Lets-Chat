package com.cdac.kp.androidchat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cdac.kp.androidchat.com.cdac.kp.androidchat.serve.MessagingService;
import com.cdac.kp.androidchat.interfaces.Manager;
import com.cdac.kp.androidchat.toolBox.StorageManipulator;
import com.cdac.kp.androidchat.type.InfoOfFriends;

import org.dmc.cdac.myapplication.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by ANKUR on 19-07-2016.
 */
public class PerformingMessage extends Activity
{
    public static final int MESSAGE_NOT_SENT = 0;
    public TextView messageBox;
    public EditText sendMessage;
    public Button send;

    public Manager serviceProvider;

    public InfoOfFriends friends = new InfoOfFriends();
    public StorageManipulator localDataStorage;

    public Cursor cursor;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceProvider = ((MessagingService.IMBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceProvider = null;
            Toast.makeText(PerformingMessage.this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();

        }
    };
    private Object appendTomessageHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.messagebox1);
        messageBox = (EditText) findViewById(R.id.messageHistoryBox);
        sendMessage = (EditText) findViewById(R.id.sendMessage);
        sendMessage.requestFocus();

        send = (Button) findViewById(R.id.send);

        Bundle extras = this.getIntent().getExtras();

        friends.userName = extras.getString(InfoOfFriends.Username);
        friends.ip = extras.getString(InfoOfFriends.Ip);
        friends.port = extras.getString(InfoOfFriends.Port);
        String msg = extras.getString(InfoOfFriends.Message);

        setTitle("Messaging With" + friends.userName);

        localDataStorage = new StorageManipulator(this, msg, null, 0);
        cursor = localDataStorage.get(friends.userName, MessagingService.USERNAME);

        if (cursor.getCount() > 0) {
            int noOfScorer = 0;
            cursor.moveToFirst();
            while ((!cursor.isAfterLast()) && noOfScorer < cursor.getCount())
            {
                noOfScorer++;

                this.appendTomessageHistory(cursor.getString(2), cursor.getString(3));
                cursor.moveToNext();
            }
        }
        localDataStorage.close();

        if (msg != null)
        {
            this.appendTomessageHistory(friends.userName, msg);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friends.userName+msg).hashCode());
        }

        send.setOnClickListener(new View.OnClickListener()
        {
            CharSequence message;
            Handler handler = new Handler();

            @Override
            public void onClick(View v)
            {
                message = sendMessage.getText();
                if (message.length() >0)
                {
                    appendTomessageHistory(serviceProvider.getUsername(), message.toString());

                    localDataStorage.insert(serviceProvider.getUsername(), friends.userName, message.toString());

                    sendMessage.setText("");

                    Thread thread = new Thread()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                if (serviceProvider.sendMessage(serviceProvider.getUsername(), friends.userName, message.toString()) == false)

                                {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Message Can't be Send", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (UnsupportedEncodingException e) {
                                Toast.makeText(getApplicationContext(), "Message Can't be Send", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                        }
                    };
                    thread.start();
                }

            }
        });
    }

    public class MessageReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String username = extras.getString(InfoOfFriends.User_id);
            String message = extras.getString(InfoOfFriends.Message);

            if (username != null && message != null)
            {
                if (friends.userName.equals(username))
                {
                    appendTomessageHistory(username, message);
                    localDataStorage.insert(username, InfoOfFriends.Username, message);
                }

            }
        }
    }

    public MessageReceiver messageReceiver = new MessageReceiver();

    private void appendTomessageHistory(String username, String message)
    {
        if (username != null && message != null)
        {
            messageBox.append(username + ": \n");
            messageBox.append(message + "\n");
        }
    }
}

