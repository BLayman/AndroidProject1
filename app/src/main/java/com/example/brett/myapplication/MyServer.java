package com.example.brett.myapplication;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Brett on 3/28/2018.
 */

public class MyServer extends Thread{
    static final String TAG = "MyServer";
    static final int PORT = 5555;
    public Socket socket;
    private MainActivity parent;
    String ipAddress;

    MyServer(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public void setParent(MainActivity mainA){ parent = mainA;}

    public void run(){

            try{
                socket = new Socket(ipAddress,PORT);
                Log.d(TAG,"server listening");
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = parent.handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("started", "started server");
                        msg.setData(b);
                        parent.handler.sendMessage(msg);
                    }
                });

                byte[] buffer = new byte[1024];
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                InputStream inStream = socket.getInputStream(); // should use this to get incoming data?
                buffer[0] = 0x00; // will send zero
                pw.println(buffer);
                pw.flush();

            }catch(Exception e){
                Log.e(TAG,"error connecting");
                e.printStackTrace();
            }


    }
}
