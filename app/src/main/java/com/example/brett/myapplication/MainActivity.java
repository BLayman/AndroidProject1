
package com.example.brett.myapplication;

import android.content.ActivityNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;
import android.speech.RecognizerIntent;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyActivity";
    TTS tts;
    Client client;
    private static final int MY_DATA_CHECK_CODE = 0; // check user data for TTS
    private static final int REQ_CODE_SPEECH_INPUT = 1;
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button speakBtn = (Button) findViewById(R.id.speakButton);
        speakBtn.setOnClickListener(this);

        Button connectBtn = (Button) findViewById(R.id.connect);
        connectBtn.setOnClickListener(this);

        ImageButton micBtn = (ImageButton) findViewById(R.id.micBtn);
        micBtn.setOnClickListener(this);

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
                Log.d(TAG, "received message from Pi: " + msgData);
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
                break;
            case R.id.connect:
                client = new Client(getIpAddress());
                client.setParent(this);
                client.start();
                break;
            case R.id.micBtn:
                Log.d(TAG,"mic button pressed");
                startVoiceInput();
                break;
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to me.");
        //intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                Log.d(TAG,"voice recieved");
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    TextView voiceInputView = (TextView) findViewById(R.id.voiceInput);
                    voiceInputView.setText(result.get(0));
                }
                break;
            }
            case MY_DATA_CHECK_CODE:{
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
                break;
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
                        ipAddress = inetAddress.getHostAddress();
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
