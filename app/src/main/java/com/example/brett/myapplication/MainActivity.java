
package com.example.brett.myapplication;

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
import java.util.Locale;
import android.widget.Toast;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnInitListener {

    private static final String TAG = "MyActivity";
    private int MY_DATA_CHECK_CODE = 0; // check user data for TTS
    private TextToSpeech myTTS; // TTS object

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
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.speakButton:
                EditText text = (EditText) findViewById(R.id.textBox);
                String speech = text.getText().toString();
                speak(speech);
                //Log.d(TAG,speech);
        }
    }
    private void speak(String words) {
        // speak given string
        myTTS.speak(words, TextToSpeech.QUEUE_FLUSH, null);
    }

    // called after checkTTS intent completes action
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            // if user has TTS data, assign TTS object
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
            }
            // otherwise ask user to install TTS data
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }


            @Override
    public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    // set language to U.S. English
                    myTTS.setLanguage(Locale.US);
                }
                else if (i == TextToSpeech.ERROR) {
                    Toast.makeText(this, "Text to Speech failed.", Toast.LENGTH_LONG).show();
                }
    }
}
