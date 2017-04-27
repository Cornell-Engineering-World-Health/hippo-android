package edu.cornell.engineering.ewh.hippoandroid;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.opentok.android.OpentokError;

public class VideoCallActivity extends AppCompatActivity implements Session.SessionListener,
        Publisher.PublisherListener, Subscriber.SubscriberListener,
        Subscriber.VideoListener {

    public static final String API_KEY = "45817732";
    public static final String SESSION_ID = "2_MX40NTgxNzczMn5-MTQ5MzE0MzA3NDI4Nn5Xa3J4U2lCRnZxcVN5bUJxM0tQWlpuY0h-UH4";
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NTgxNzczMiZzaWc9NzMyYjcwZjVlMzE0MGIxNzJkNzU3OTgyNTdlZmVkNjkzMGYyOWNiOTpzZXNzaW9uX2lkPTJfTVg0ME5UZ3hOemN6TW41LU1UUTVNekUwTXpBM05ESTRObjVYYTNKNFUybENSblp4Y1ZONWJVSnhNMHRRV2xwdVkwaC1VSDQmY3JlYXRlX3RpbWU9MTQ5MzMyNjEyNCZub25jZT0wLjM1MTEzNDI3MjA1NjUyODkmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTQ5MzQxMjUyNA==";
    public static final String LOGTAG = "VideoCallActivity";

    private RelativeLayout publisherView;
    private RelativeLayout.LayoutParams publisherParams;
    private RelativeLayout subscriberView;
    private RelativeLayout.LayoutParams subscriberParams;
    private RelativeLayout buttonView;
    private RelativeLayout.LayoutParams buttonParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, "call to onCreate");
        super.onCreate(savedInstanceState);

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
       // subscriberParams.weight = 1f;
       // subscriberView.setGravity(Gravity.TOP);
        subscriberView.setLayoutParams(subscriberParams);

        publisherView = new RelativeLayout(this);
        publisherParams = new RelativeLayout.LayoutParams(240,320);
        publisherParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        publisherParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
       // publisherParams.weight = 0.2f;
        //publisherView.setGravity(Gravity.BOTTOM);
       // publisherView.setHorizontalGravity(Gravity.LEFT);
        publisherView.setLayoutParams(publisherParams);

        buttonView = new RelativeLayout(this);
        buttonParams = new RelativeLayout.LayoutParams(400,200);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonView.setLayoutParams(buttonParams);

        //parentLayout.setWeightSum(1f);
        parentLayout.addView(subscriberView);
        parentLayout.addView(publisherView);
        parentLayout.addView(buttonView);

        final Session session = new Session(VideoCallActivity.this, API_KEY, SESSION_ID);
        session.setSessionListener(this);
        session.connect(TOKEN);
    }

    @Override
    public void onConnected(final Session session) {
        Log.i(LOGTAG, "call to onConnected of the SessionListener");
        final Publisher publisher = new Publisher(VideoCallActivity.this);
        publisher.setPublisherListener(this);
        publisherView.addView(publisher.getView());
        session.publish(publisher);

        ToggleButton endCall = new ToggleButton(this);
        endCall.setChecked(true);
        endCall.setText("Video Off");
        endCall.setTextOn("Video Off");
        endCall.setTextOff("Video On");
        endCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.w(LOGTAG, "PUBLISHING");
                    //session.publish(publisher);
                    publisher.setPublishVideo(true);
                }
                else {
                    Log.w(LOGTAG, "UNPUBLISHING");
                    //session.unpublish(publisher);
                    publisher.setPublishVideo(false);
                }
            }
        });
        buttonView.addView(endCall);
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOGTAG, "call to onStreamReceived");
        Subscriber subscriber = new Subscriber(VideoCallActivity.this, stream);
        subscriber.setVideoListener(this);
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
