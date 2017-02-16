package com.cdac.kp.androidchat.interfaces;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ANKUR on 20-07-2016.
 */
public interface Socket
{
    public String sendHttpRequest(String params);
    public int startListeningPort(int port);

    public void stopListening();
    public void exit();

    public boolean sendMessage(String message, String ip, int port);

    InputStream getInputStream();
    OutputStream getOutputStream();

    void shutdownInput();

    void shutdownOutput();

    void close();



}
