package com.example.brett.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Brett on 3/27/2018.
 */

public class TTS extends Thread implements TextToSpeech.OnInitListener{
    private static final String TAG = "TTS";

    private TextToSpeech tts;

    private Context context;
    public Handler handler;
    private String last;

    TTS(Context con){
        context = con;
        last = "";
        tts = new TextToSpeech(context, this);
    }


    public void run(){
        Looper.prepare();
        handler = new Handler(){
            public void handleMessage(Message msg){

                String msgData = msg.getData().getString("TT");
                Log.d(TAG,msgData);
                speak(msgData);
            }
        };

        Looper.loop();
    }

    public void speak(String words) {
        if(last != words){
            last = words;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                tts.speak(words, TextToSpeech.QUEUE_FLUSH, null, null);
            }
            else {
                tts.speak(words, TextToSpeech.QUEUE_FLUSH, null);
            }

            while (tts.isSpeaking()){
                try {
                    Thread.sleep(200);
                } catch (Exception e){}
            }
        }
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            // set language to U.S. English
            tts.setLanguage(Locale.US);
        }
        else if (i == TextToSpeech.ERROR) {
            Toast.makeText(context, "Text to Speech failed.", Toast.LENGTH_LONG).show();
        }
    }
}
