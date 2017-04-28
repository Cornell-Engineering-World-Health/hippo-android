package edu.cornell.engineering.ewh.hippoandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class VideoCallActivityTemp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_temp);

        //TODO add these lines below
        Intent intent = getIntent();
        String sessionName = intent.getStringExtra(MainActivity.SESSION_NAME);
        //**

        //For testing
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(sessionName);

    }
}
