
package com.example.brett.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.speakButton);
        button.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.speakButton:
                EditText text = (EditText) findViewById(R.id.textBox);
                Log.d(TAG,text.getText().toString());

        }
    }



}
