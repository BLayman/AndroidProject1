package com.example.brett.Robot_Control;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Brett on 3/28/2018.
 */

public class Client extends Thread{
    TTS tts;

    public Client(TTS tts) {
        this.tts = tts;
    }

    @Override
    public void run(){
        try{
            Socket clientSocket = new Socket("192.168.1.8", 9999);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String in = input.readLine();
            clientSocket.close();

            if(in != null) {
                Message sendMsg = tts.handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("TT", in);
                sendMsg.setData(b);
                tts.handler.sendMessage(sendMsg);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
