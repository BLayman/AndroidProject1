
package com.example.brett.myapplication;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import android.widget.Toast;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyActivity";
    TTS tts;
    MyServer server;
    private int MY_DATA_CHECK_CODE = 0; // check user data for TTS
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.speakButton);
        button.setOnClickListener(this);

        // create intent for TTS
        Intent checkTTSIntent = new Intent();
        // set intent action to check TTS data
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        // start above action, passing in check data variable
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        handleSocketMessages();


    }

    private void handleSocketMessages(){

        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                String msgData = msg.getData().getString("started");
                Log.d(TAG, "received message from MyServer: " + msgData);
            }
        };
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.speakButton:
                EditText text = (EditText) findViewById(R.id.textBox);
                String speech = text.getText().toString();
                Message sendMsg = tts.handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("TT",speech);
                sendMsg.setData(b);

                tts.handler.sendMessage(sendMsg);
                //Log.d(TAG,speech);
            case R.id.connect:
                server = new MyServer(getIpAddress());
                server.setParent(this);
                server.start();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            // if user has TTS data, assign TTS object
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                Log.d(TAG,"tts found");
                tts = new TTS(this);
                tts.start();
            }
            // otherwise ask user to install TTS data
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public static String getIpAddress() {
        String ipAddress = "Unable to Fetch IP..";
        try {
            Enumeration en;
            en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ipAddress = inetAddress.getHostAddress().toString();
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return ipAddress;
    }

}
