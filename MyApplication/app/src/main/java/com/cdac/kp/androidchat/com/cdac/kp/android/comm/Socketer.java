package com.cdac.kp.androidchat.com.cdac.kp.android.comm;

import android.util.Log;

import com.cdac.kp.androidchat.interfaces.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ANKUR on 20-07-2016.
 */
public class Socketer implements Socket
{
    private static final  String AUTHENTICATION_SERVER_ADDRESS = "112.133.248.124/androidchat/";
    private int listeningPort = 0;

    private static final String HTTP_REQUEST_FAILED = null;

    private HashMap<InetAddress, Socket> sockets = new HashMap<InetAddress, Socket>();

    private ServerSocket serverSocket = null;
    private  boolean listening;



    private class ReceiveConnection extends Thread
    {
        Socket clientSocket = null;
        public ReceiveConnection(Socket socket)
        {
            this.clientSocket = socket;
            Socketer.this.sockets.put(((ServerSocket) socket).getInetAddress(), socket);
        }

        @Override
        public void run()
        {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    if (inputLine.equals("exit") == false)
                    {

                    }
                    else
                    {
                        clientSocket.shutdownInput();
                        clientSocket.shutdownOutput();
                        clientSocket.close();
                        Socketer.this.sockets.remove(((ServerSocket) clientSocket).getInetAddress());
                    }
                }
            }catch (IOException e)
            {
                Log.e("Receiver Connection.run: when receiving connection ","");
            }
            super.run();
        }
    }

    @Override
    public String sendHttpRequest(String params)
    {
        URL url;
        String result = new String();
        try {
            url = new URL(AUTHENTICATION_SERVER_ADDRESS);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);

            PrintWriter out = new PrintWriter(connection.getOutputStream());

            out.println(params);
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                result = result.concat(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result.length() == 0)
        {
            result = HTTP_REQUEST_FAILED;
        }
        return result;
    }

    @Override
    public int startListeningPort(int portNo)
    {
        listening = true;
        try {
            serverSocket = new ServerSocket(portNo);
            this.listeningPort = portNo;
        }catch (IOException e) {
            return 0;
        }
        while (listening)
        {
            try {
                new ReceiveConnection((Socket) serverSocket.accept()).start();

            }catch (IOException e)
            {
                return 2;
            }
        }
        try {
            serverSocket.close();

        }
        catch (IOException e)
        {
            Log.e("Exception server socket","Exception when closing server socket");
            return 3;
        }
        return 1;
    }


    @Override
    public void stopListening()
    {
        this.listening = false;
    }

    @Override
    public void exit()
    {
        for (Iterator<Socket> iterator = sockets.values().iterator();iterator.hasNext();)
        {
            Socket socket = iterator.next();
            try
            {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();

            }
            catch (Exception e)
            {

            }
        }
    }

    @Override
    public boolean sendMessage(String message, String ip, int port)
    {
        try {


            String[] str = ip.split("\\.");

            byte[] IP = new byte[str.length];

            for (int i = 0; i < str.length; i++) {

                IP[i] = (byte) Integer.parseInt(str[i]);
            }
            Socket socket = getSocket(InetAddress.getByAddress(IP), port);
            if (socket == null) {
                return false;
            }

            PrintWriter out = null;
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(message);
        } catch (UnknownHostException e) {
            return false;
            //e.printStackTrace();
        } catch (IOException e) {
            return false;
            //e.printStackTrace();
        }

        return true;

    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public void shutdownInput() {

    }

    @Override
    public void shutdownOutput() {

    }

    @Override
    public void close() {

    }

    private Socket getSocket(InetAddress addr, int portNo) //private Socket getSocket(InetAddress addr, int portNo)
    {
        return null;
    }

    public int getListeningPort()
    {
        return this.listeningPort;
    }

}
