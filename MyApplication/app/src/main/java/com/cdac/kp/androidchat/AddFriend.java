package com.cdac.kp.androidchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cdac.kp.androidchat.com.cdac.kp.androidchat.serve.MessagingService;
import com.cdac.kp.androidchat.interfaces.Manager;

import org.dmc.cdac.myapplication.R;



/**
 * Created by ANKUR on 19-07-2016.
 */
public class AddFriend extends Activity implements View.OnClickListener
{
    public EditText username;
    public Button add;
    public Button cancel;

    protected static final int TYPE_FRIEND_USERNAME = 0;

    public Manager serviceProvider;

    @Override
    public void onClick(View v)
    {
        if (v == cancel)
        {
            finish();
        }
        else if (v == add)
        {

        }
    }

    private void addNewFriend()
    {
        if (username.length() >0)
        {
            Thread thread = new Thread()
            {
                @Override
                public void run() {
                    serviceProvider.addNewFriendRequest(username.getText().toString());
                }
            };
            thread.start();

            Toast.makeText(AddFriend.this, "Request Sent", Toast.LENGTH_SHORT).show();

            finish();
        }
    }


    protected Dialog onCreateDialog(int id)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFriend.this);
        if (id == TYPE_FRIEND_USERNAME)
        {
            builder.setTitle(R.string.add_new_friend)
                    .setMessage("Friend Username");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        return builder.create();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friendfinderadder);
        setTitle("Add Friend");

        add = (Button)findViewById(R.id.addFriend);
        cancel = (Button)findViewById(R.id.cancel);
        username = (EditText)findViewById(R.id.newFriendUsername);

        if (add != null)
        {
            add.setOnClickListener(this);
        }
        if (cancel != null)
        {
            cancel.setOnClickListener(this);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceProvider = ((MessagingService.IMBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (serviceProvider != null)
            {
                serviceProvider = null;
            }
            Toast.makeText(AddFriend.this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
        }
    };


}
