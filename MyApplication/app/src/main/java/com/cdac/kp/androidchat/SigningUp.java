package com.cdac.kp.androidchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cdac.kp.androidchat.interfaces.Manager;

import org.dmc.cdac.myapplication.R;

/**
 * Created by ANKUR on 20-07-2016.
 */
public class SigningUp extends Activity
{
    public static final int FILL_ALL_FIELDS = 0;
    public static final int TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS = 1;
    public static final int SIGN_UP_FAILED = 2;
    public static final int SIGN_UP_SUCCESSFULL = 3;
    private static final int SIGN_UP_USERNAME_CRASHED = 4;
    public static final int USERNAME_AND_PASSWORD_LENGTH_SHORT = 5;

    public static final String SERVER_SIGNUP_SUCCESFUL = "6";
    public static final String SERVER_SIGNUP_CRASHED = "7";



    private EditText username;
    private EditText password;
    private EditText password_confirm;
    private EditText email;

    private Button signUpButton;
    private Button loginButton;

    public Manager serviceProvide;
    public Handler handler;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.signingup);
        setTitle("Sign up");

        Button signUpButton = (Button) findViewById(R.id.signUp_btn);

        username = (EditText) findViewById(R.id.username_registration);
        password = (EditText) findViewById(R.id.password_registration);
        password_confirm = (EditText) findViewById(R.id.confirm_password);
        email = (EditText) findViewById(R.id.email_registration);

        loginButton = (Button)findViewById(R.id.login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SigningUp.this, LogginIn.class);
                startActivity(i);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (username.length() > 0 && password.length() > 0 && password_confirm.length() > 0 && email.length() > 0) {
                    if (password.getText().toString().equals(password_confirm.getText().toString())) {

                        if (username.length() >= 5 && password.length() >= 5) {
                            Thread thread = new Thread() {
                                String result = new String();

                                @Override
                                public void run() {
                                    result = serviceProvide.signUpUser(username.getText().toString(), password.getText().toString(), email.getText().toString());

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result.equals(SERVER_SIGNUP_SUCCESFUL)) {
                                                Toast.makeText(getApplicationContext(), R.string.signup_successfull, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), R.string.signup_failed, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            };
                            thread.start();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.username_and_password_length_short, Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.signup_type_same_password_in_password_fields, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.signup_fill_all_fields, Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS:
                return new AlertDialog.Builder(SigningUp.this)
                        .setMessage(R.string.signup_type_same_password_in_password_fields)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
                            }
                        })
                        .create();
            case FILL_ALL_FIELDS:
                return new AlertDialog.Builder(SigningUp.this)
                        .setMessage(R.string.signup_fill_all_fields)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
                            }
                        })
                        .create();
            case SIGN_UP_FAILED:
                return new AlertDialog.Builder(SigningUp.this)
                        .setMessage(R.string.signup_failed)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
                            }
                        })
                        .create();
            case SIGN_UP_USERNAME_CRASHED:
                return new AlertDialog.Builder(SigningUp.this)
                        .setMessage(R.string.signup_username_crashed)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
                            }
                        })
                        .create();
            case SIGN_UP_SUCCESSFULL:
                return new AlertDialog.Builder(SigningUp.this)
                        .setMessage(R.string.signup_successfull)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                        .create();
            case USERNAME_AND_PASSWORD_LENGTH_SHORT:
                return new AlertDialog.Builder(SigningUp.this)
                        .setMessage(R.string.username_and_password_length_short)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
                            }
                        })
                        .create();
            default:
                return null;

        }
    }
}
