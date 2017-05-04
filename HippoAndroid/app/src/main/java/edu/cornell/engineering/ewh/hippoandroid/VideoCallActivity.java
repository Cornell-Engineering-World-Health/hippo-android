package edu.cornell.engineering.ewh.hippoandroid;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.util.DisplayMetrics;
import android.text.style.ImageSpan;
import 	android.text.SpannableString;


import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.opentok.android.OpentokError;
import com.opentok.android.VideoUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class VideoCallActivity extends AppCompatActivity implements Session.SessionListener,
        Publisher.PublisherListener, Subscriber.SubscriberListener,
        Subscriber.VideoListener, AsyncResponse {

    public static final String API_KEY = "45817732";
    public static String SESSION_ID; //= "2_MX40NTgxNzczMn5-MTQ5MzE0MzA3NDI4Nn5Xa3J4U2lCRnZxcVN5bUJxM0tQWlpuY0h-UH4";
    public static String TOKEN; //= "T1==cGFydG5lcl9pZD00NTgxNzczMiZzaWc9YzNkZDBhZjdkNTcyMDc3MTRiODZlMzg4ZjA3NzY4Y2YzYmM1NDliNTpzZXNzaW9uX2lkPTJfTVg0ME5UZ3hOemN6TW41LU1UUTVNekUwTXpBM05ESTRObjVYYTNKNFUybENSblp4Y1ZONWJVSnhNMHRRV2xwdVkwaC1VSDQmY3JlYXRlX3RpbWU9MTQ5MzU4NTczNCZub25jZT0wLjgyODUwNTMxMzc3MjExMTcmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTQ5MzY3MjEzNA==";
    public static final String LOGTAG = "VideoCallActivity";

    private RelativeLayout publisherView;
    private RelativeLayout.LayoutParams publisherParams;
    private RelativeLayout subscriberView;
    private RelativeLayout.LayoutParams subscriberParams;
    private RelativeLayout buttonView;
    private RelativeLayout.LayoutParams buttonParams;
    private int height;
    private int width;
    private int button_height;
    private int button_width;
    private Drawable delete;
    private Drawable camera;
    private Drawable mute;
    private Drawable unmute;
    AsyncCall getSession = new AsyncCall();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, "call to onCreate");
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Intent intent = getIntent();
        String sessionName = intent.getStringExtra(MainActivity.SESSION_NAME);

        /*get screen size*/
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        button_height = (int) height/7;//0.167 * height;
        button_width = (int) width/3;//0.333 * width;

        Log.i(LOGTAG, "height : " + height);
        Log.i(LOGTAG, "width : " + width);
        Log.i(LOGTAG, "button_height : " + button_height);
        Log.i(LOGTAG, "button_width : " + button_width);

        //this to set delegate/listener back to this class
        getSession.delegate = this;

        //execute the async task
        getSession.execute("https://ewh-hippo.herokuapp.com/api/videos/" + sessionName);
    }

    public void processFinish(String output) {

        try{
            JSONObject jsonObject = new JSONObject(output);
            SESSION_ID = jsonObject.getString("sessionId");
            TOKEN = jsonObject.getString("tokenId");

            RelativeLayout parentLayout = new RelativeLayout(this);
            RelativeLayout.LayoutParams parentParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            parentLayout.setLayoutParams(parentParams);
            setContentView(parentLayout);

            subscriberView = new RelativeLayout(this);
            subscriberParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            subscriberParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            subscriberParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            subscriberView.setLayoutParams(subscriberParams);

            publisherView = new RelativeLayout(this);
            publisherParams = new RelativeLayout.LayoutParams(240,320);
            publisherParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            publisherParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            publisherView.setLayoutParams(publisherParams);

            buttonView = new RelativeLayout(this);
            buttonParams = new RelativeLayout.LayoutParams(width,button_height);
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            buttonView.setLayoutParams(buttonParams);

            parentLayout.addView(subscriberView);
            parentLayout.addView(publisherView);
            parentLayout.addView(buttonView);

            final Session session = new Session(VideoCallActivity.this, API_KEY, SESSION_ID);
            session.setSessionListener(this);
            session.connect(TOKEN);

        } catch(Exception e) {
            Log.d("processFinish", "Error while processing JSON: "+e.getMessage());
        }
    }

    @Override
    public void onConnected(final Session session) {
        Log.i(LOGTAG, "call to onConnected of the SessionListener");
        final Publisher publisher = new Publisher(VideoCallActivity.this);
        publisher.setPublisherListener(this);
        publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);
        publisherView.addView(publisher.getView());
        //publisherView.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
        session.publish(publisher);
        Resources res = getResources();
        delete =res.getDrawable(android.R.drawable.ic_menu_camera);
        camera =res.getDrawable(android.R.drawable.ic_menu_delete);
        unmute = res.getDrawable(android.R.drawable.ic_btn_speak_now);

        final ToggleButton toggleVideo = new ToggleButton(this);
        /*ImageSpan cameraSpan = new ImageSpan(this, android.R.drawable.ic_menu_camera);
        ImageSpan blockSpan = new ImageSpan(this, android.R.drawable.ic_menu_delete);//ic_menu_block
        SpannableString cameraString = new SpannableString("X");
        cameraString.setSpan(cameraSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString blockString = new SpannableString("X");
        cameraString.setSpan(blockSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); */

        toggleVideo.setTextOff("");
        toggleVideo.setTextOn("");
        toggleVideo.setText("");
        toggleVideo.setChecked(true);
        toggleVideo.setBackgroundDrawable(delete);


        //toggleVideo.setText(blockString);
       // toggleVideo.setButtonDrawable(res.getDrawable(android.R.drawable.ic_menu_camera));
        //toggleVideo.setTextOn(cameraString);
        //toggleVideo.setTextOff(blockString);
        toggleVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.i(LOGTAG, "PUBLISHING");
                    publisher.setPublishVideo(true);
                    toggleVideo.setBackgroundDrawable(delete);
                }
                else {
                    Log.i(LOGTAG, "UNPUBLISHING");
                    publisher.setPublishVideo(false);
                    toggleVideo.setBackgroundDrawable(camera);
                }
            }
        });
        RelativeLayout.LayoutParams toggleVideoParams = new RelativeLayout.LayoutParams(button_width,button_height);
        toggleVideoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        toggleVideo.setLayoutParams(toggleVideoParams);
        buttonView.addView(toggleVideo);

        ToggleButton toggleAudio = new ToggleButton(this);
        toggleAudio.setChecked(true);
        toggleAudio.setText("Mute");
        toggleAudio.setTextOn("Mute");
        toggleAudio.setTextOff("Unmute");
        toggleAudio.setButtonDrawable(res.getDrawable(android.R.drawable.ic_btn_speak_now));
        toggleAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    publisher.setPublishAudio(true);
                }
                else {
                    publisher.setPublishAudio(false);
                }
            }
        });
        RelativeLayout.LayoutParams toggleAudioParams = new RelativeLayout.LayoutParams(button_width,button_height);
        toggleAudioParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        toggleAudio.setLayoutParams(toggleAudioParams);
        buttonView.addView(toggleAudio);

        Button endCall = new Button(this);
        //endCall.setText("End Call");
        endCall.setBackground(res.getDrawable(android.R.drawable.ic_menu_call));
        endCall.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                publisher.destroy();
                Intent intent = new Intent(VideoCallActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout.LayoutParams endCallParams = new RelativeLayout.LayoutParams(button_width,button_height);
        endCallParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        endCall.setLayoutParams(endCallParams);
        buttonView.addView(endCall);
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOGTAG, "call to onStreamReceived");
        Subscriber subscriber = new Subscriber(VideoCallActivity.this, stream);
        subscriber.setVideoListener(this);
        subscriber.setPreferredResolution(new VideoUtils.Size(width,height));
        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);
        session.subscribe(subscriber);
        subscriberView.addView(subscriber.getView());
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOGTAG, "call to onDisconnected of the SessionListener");
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOGTAG, "call to onStreamDropped of the SessionListener");
    }

    @Override
    public void onError(Session session, OpentokError error) {
        Log.i(LOGTAG, "SessionListener error: " + error.getMessage());
    }

    @Override
    public void onStreamCreated(PublisherKit publisher, Stream stream) {
        Log.i(LOGTAG, "call to onStreamCreated of the PublisherListener");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisher, Stream stream) {
        Log.i(LOGTAG, "call to onStreamDestroyed of the PublisherListener");
    }

    @Override
    public void onError(PublisherKit publisher, OpentokError error) {
        Log.i(LOGTAG, "PublisherListener error: " + error.getMessage());
    }

    @Override
    public void onConnected(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onConnected of the SubscriberListener");
    }

    @Override
    public void onDisconnected(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onDisconnected of the SubscriberListener");
    }

    @Override
    public void onError(SubscriberKit subscriber, OpentokError error) {
        Log.i(LOGTAG, "SubscriberListener error: " + error.getMessage());
    }

    @Override
    public void onVideoDataReceived(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onVideoDataReceived of the VideoListener");
    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriber, java.lang.String reason) {
        Log.i(LOGTAG, "call to onVideoDisabled of the VideoListener");
    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriber, java.lang.String reason) {
        Log.i(LOGTAG, "call to onVideoEnabled of the VideoListener");
    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onVideoDisableWarning of the VideoListener");
    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onVideoDisableWarning of the VideoListener");
    }
}
