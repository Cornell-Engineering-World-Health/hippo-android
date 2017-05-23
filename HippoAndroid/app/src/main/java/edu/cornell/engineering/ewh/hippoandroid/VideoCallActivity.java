package edu.cornell.engineering.ewh.hippoandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/*
 * Video call view displays the interface for a single call.
 * Includes basic interface controls: video on/off, mute, end call.
 */
public class VideoCallActivity extends AppCompatActivity implements Session.SessionListener,
        Publisher.PublisherListener, Subscriber.SubscriberListener,
        Subscriber.VideoListener, AsyncResponse {

    public static final String API_KEY = "45843012"; // Change to your own API key
    public static String SESSION_ID;
    public static String TOKEN;
    public static final String LOGTAG = "VideoCallActivity";

    private RelativeLayout publisherView;
    private RelativeLayout.LayoutParams publisherParams;
    private RelativeLayout subscriberView;
    private RelativeLayout.LayoutParams subscriberParams;
    private RelativeLayout buttonView;
    private RelativeLayout.LayoutParams buttonParams;
    private Subscriber subs;
    private int height;
    private int width;
    private int buttonViewWidth;
    private int button_height;
    private int button_width;
    private Drawable delete;
    private Drawable camera;
    private Drawable mute;
    private Drawable unmute;
    private Drawable endcall;
    AsyncCall getSession = new AsyncCall();

    /*
    Sets up the screen size values and makes the API call for
    that session
     */
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

        // get screen size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        buttonViewWidth = (int) (width*.75);

        // set height and width of the end call, audio toggle, and video toggle buttons
        button_height = (int) height/8;
        button_width = (int) width/5;

        Log.i(LOGTAG, "height : " + height);
        Log.i(LOGTAG, "width : " + width);
        Log.i(LOGTAG, "button_height : " + button_height);
        Log.i(LOGTAG, "button_width : " + button_width);

        // this to set delegate/listener back to this class
        getSession.delegate = this;

        // API call to get the session
        SharedPreferences sharedPreferences = this.getSharedPreferences("APP", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Authorization", "default, means there was no G_TOKEN");
        getSession.execute("https://ewh-hippo.herokuapp.com/api/videos/" + sessionName, token);

    }

    /*
     * Called when the API call has been made
     */
    public void processFinish(String output) {

        try{
            // data returned from the server
            JSONObject jsonObject = new JSONObject(output);
            SESSION_ID = jsonObject.getString("sessionId");
            TOKEN = jsonObject.getString("tokenId");

            // create the layout views
            RelativeLayout parentLayout = new RelativeLayout(this);
            RelativeLayout.LayoutParams parentParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            parentLayout.setLayoutParams(parentParams);
            setContentView(parentLayout);

            // view for the other person's video
            subscriberView = new RelativeLayout(this);
            subscriberParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            subscriberParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            subscriberParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            subscriberView.setLayoutParams(subscriberParams);

            // view for your video
            publisherView = new RelativeLayout(this);
            publisherParams = new RelativeLayout.LayoutParams(240,320);
            publisherParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            publisherParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            publisherView.setLayoutParams(publisherParams);

            // view for the buttons
            buttonView = new RelativeLayout(this);
            buttonParams = new RelativeLayout.LayoutParams(buttonViewWidth,button_height);
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            buttonView.setLayoutParams(buttonParams);

            // adding the views to the screen
            parentLayout.addView(subscriberView);
            parentLayout.addView(publisherView);
            parentLayout.addView(buttonView);

            // connect to the session with the ID and TOKEN returned from the API call
            final Session session = new Session(VideoCallActivity.this, API_KEY, SESSION_ID);
            session.setSessionListener(this);
            session.connect(TOKEN);

        } catch(Exception e) {
            Log.d("processFinish", "Error while processing JSON: "+e.getMessage());
        }
    }

    /*
     * Called upon successful connection to an OpenTOK session
     */
    @Override
    public void onConnected(final Session session) {
        Log.i(LOGTAG, "call to onConnected of the SessionListener");

        // add your video stream to the view
        final Publisher publisher = new Publisher(VideoCallActivity.this);
        publisher.setPublisherListener(this);
        publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);
        publisherView.addView(publisher.getView());
        session.publish(publisher);
        Resources res = getResources();

        // add logos to the buttons
        delete =res.getDrawable(R.drawable.ic_videocam_off_black_48dp);
        camera =res.getDrawable(R.drawable.ic_videocam_black_48dp);
        unmute = res.getDrawable(R.drawable.ic_mic_off_black_48dp);
        mute = res.getDrawable(R.drawable.ic_mic_black_48dp);
        endcall = res.getDrawable(R.drawable.ic_call_end_black_48dp);

        // set up the video toggle button
        final ToggleButton toggleVideo = new ToggleButton(this);
        toggleVideo.setTextOff("");
        toggleVideo.setTextOn("");
        toggleVideo.setText("");
        toggleVideo.setChecked(true);
        toggleVideo.setBackgroundDrawable(camera);
        toggleVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.i(LOGTAG, "PUBLISHING");
                    publisher.setPublishVideo(true);
                    toggleVideo.setBackgroundDrawable(camera);
                }
                else {
                    Log.i(LOGTAG, "UNPUBLISHING");
                    publisher.setPublishVideo(false);
                    toggleVideo.setBackgroundDrawable(delete);
                }
            }
        });
        RelativeLayout.LayoutParams toggleVideoParams = new RelativeLayout.LayoutParams(button_width,button_height);
        toggleVideoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        toggleVideo.setLayoutParams(toggleVideoParams);
        buttonView.addView(toggleVideo);

        // set up the audio toggle button
        final ToggleButton toggleAudio = new ToggleButton(this);
        toggleAudio.setChecked(true);
        toggleAudio.setText("");
        toggleAudio.setTextOn("");
        toggleAudio.setTextOff("");
        toggleAudio.setBackgroundDrawable(mute);
        toggleAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    publisher.setPublishAudio(true);
                    toggleAudio.setBackgroundDrawable(mute);
                }
                else {
                    publisher.setPublishAudio(false);
                    toggleAudio.setBackgroundDrawable(unmute);
                }
            }
        });
        RelativeLayout.LayoutParams toggleAudioParams = new RelativeLayout.LayoutParams(button_width,button_height);
        toggleAudioParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        toggleAudio.setLayoutParams(toggleAudioParams);
        buttonView.addView(toggleAudio);

        // set up the end call button
        Button endCall = new Button(this);
        endCall.setBackgroundDrawable(endcall);
        endCall.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                publisher.destroy();
                if(subs != null)
                    subs.destroy();
                Intent intent = new Intent(VideoCallActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout.LayoutParams endCallParams = new RelativeLayout.LayoutParams(button_width,button_height);
        endCallParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        endCall.setLayoutParams(endCallParams);
        buttonView.addView(endCall);
    }

    /*
     * Called when another user joins the session
     */
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        // add the video stream of the other user to the view
        Log.i(LOGTAG, "call to onStreamReceived");
        Subscriber subscriber = new Subscriber(VideoCallActivity.this, stream);
        subscriber.setVideoListener(this);
        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);
        session.subscribe(subscriber);
        subs = subscriber;
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
