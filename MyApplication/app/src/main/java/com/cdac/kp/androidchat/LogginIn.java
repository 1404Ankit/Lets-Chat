package com.cdac.kp.androidchat;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.cdac.kp.androidchat.com.cdac.kp.androidchat.serve.MessagingService;
import com.cdac.kp.androidchat.interfaces.Manager;

import org.dmc.cdac.myapplication.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by ANKUR on 19-07-2016.
 */
public class LogginIn extends Activity
{
    public static final int NOT_CONNECTED_TO_SERVICE = 0;
    public static final int FILL_BOTH_USERNAME_PASSWORD = 1;
    public static final String AUTHENTICATION_FAILED = "failed";
    public static final String FRIEND_LIST = "friend_list";
    public static final int MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT = 2;
    public static final int NOT_CONNECTED_TO_NETWORK = 3;

    public EditText usernameText;
    public EditText passwordText;
    public Button loginButton;
    public TextView signup;

    public Manager serviceProvider;

    public static final int SIGN_UP_ID = Menu.FIRST;
    public static final int EXIT_APP_ID = Menu.FIRST + 1;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceProvider = ((MessagingService.IMBinder)service).getService();

            if (serviceProvider.isUserAuthenticate() == true)
            {
                Intent i = new Intent(LogginIn.this, ListOfFriends.class);
                startActivity(i);
                LogginIn.this.finish();
            }
        }



        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            serviceProvider = null;
            Toast.makeText(LogginIn.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        startService(new Intent(LogginIn.this, MessagingService.class));


        setContentView(R.layout.loggin_in);
        setTitle("Login");

        loginButton = (Button) findViewById(R.id.btn_login);
        signup = (TextView)findViewById(R.id.textViewSignup);
        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (serviceProvider == null)
                {
                    Toast.makeText(getApplicationContext(),R.string.not_connected_to_service, Toast.LENGTH_LONG).show();
                    return;
                }
                else if (serviceProvider.isNetworkConnected() == false)
                {
                    Toast.makeText(getApplicationContext(),R.string.not_connected_to_service, Toast.LENGTH_LONG).show();

                }
                else if (usernameText.length() > 0 && passwordText.length() >0)
                {
                    Thread loginThread = new Thread()
                    {
                        private Handler handler = new Handler();

                        @Override
                        public void run()
                        {
                            String result = null;
                            try
                            {
                                result = serviceProvider.authenticateUser(usernameText.getText().toString(), passwordText.getText().toString());
                            }
                            catch (UnsupportedEncodingException e)
                            {
                                e.printStackTrace();
                            }
                            if (result == null || result.equals(AUTHENTICATION_FAILED))
                            {
                                handler.post(new Runnable()
                                {
                                    public void run()
                                    {
                                        Toast.makeText(getApplicationContext(),R.string.make_sure_username_and_password_correct, Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                            else
                            {
                                handler.post(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Intent i =new Intent(LogginIn.this, ListOfFriends.class);

                                        startActivity(i);
                                        LogginIn.this.finish();
                                    }
                                });
                            }
                        }
                    };
                    loginThread.start();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.fill_both_username_and_password, Toast.LENGTH_LONG).show();
                }
            }

        });

        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(LogginIn.this, SigningUp.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        int message = -1;
        switch (id)
        {
            case NOT_CONNECTED_TO_SERVICE:
                message = R.string.not_connected_to_service;
                break;
            case FILL_BOTH_USERNAME_PASSWORD:
                message = R.string.fill_both_username_and_password;
                break;
            case MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT:
                message = R.string.make_sure_username_and_password_correct;
                break;
            case NOT_CONNECTED_TO_NETWORK:
                message = R.string.not_connected_to_network;
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean result = super.onCreateOptionsMenu(menu);

        menu.add(0, SIGN_UP_ID, 0, R.string.sign_up);
        menu.add(0, EXIT_APP_ID, 0, R.string.exit_application);


        return result;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch(item.getItemId())
        {
            case SIGN_UP_ID:
                Intent i = new Intent(LogginIn.this, SigningUp.class);
                startActivity(i);
                return true;
            case EXIT_APP_ID:
                signup.performClick();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
}
