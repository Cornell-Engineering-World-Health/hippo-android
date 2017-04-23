package edu.cornell.engineering.ewh.hippoandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.util.Log;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.opentok.android.OpentokError;

public class MainActivity extends AppCompatActivity implements Session.SessionListener,
        Publisher.PublisherListener, Subscriber.SubscriberListener,
        Subscriber.VideoListener {

    public static final String API_KEY = "45817732";
    public static final String SESSION_ID = "1_MX40NTgxNzczMn5-MTQ5Mjk4MDYyNjI5M34yUGRpZUY5cG5MZ0JoRmwwWGp6c2FxZ2J-UH4";
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NTgxNzczMiZzaWc9ZGYzNGI3NmJmMGVhOTU3MTFkMmRkNDY1YWQ3OGQ1MmI2OGJjMDEyYjpzZXNzaW9uX2lkPTFfTVg0ME5UZ3hOemN6TW41LU1UUTVNams0TURZeU5qSTVNMzR5VUdScFpVWTVjRzVNWjBKb1Jtd3dXR3A2YzJGeFoySi1VSDQmY3JlYXRlX3RpbWU9MTQ5Mjk4MDYyNiZub25jZT0wLjkxNjQwOTg0NjA5NjI3MTcmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTQ5MzA2NzAyNg==";
    public static final String LOGTAG = "MainActivity";

    private LinearLayout publisherView;
    private LinearLayout.LayoutParams publisherParams;
    private LinearLayout subscriberView;
    private LinearLayout.LayoutParams subscriberParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, "call to onCreate");
        super.onCreate(savedInstanceState);

        LinearLayout parentLayout = new LinearLayout(this);
        setContentView(parentLayout);

        subscriberView = new LinearLayout(this);
        subscriberParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subscriberParams.weight = 0.5f;
        subscriberView.setLayoutParams(subscriberParams);

        publisherView = new LinearLayout(this);
        publisherParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        publisherParams.weight = 0.5f;
        publisherView.setLayoutParams(publisherParams);

        parentLayout.setWeightSum(1f);
        parentLayout.addView(publisherView);
        parentLayout.addView(subscriberView);

        Session session = new Session(MainActivity.this, API_KEY, SESSION_ID);
        session.setSessionListener(this);
        session.connect(TOKEN);
    }

    @Override
    public void onConnected(Session session) {
        Log.i(LOGTAG, "call to onConnected of the SessionListener");
        Publisher publisher = new Publisher(MainActivity.this);
        publisher.setPublisherListener(this);
        publisherView.addView(publisher.getView(), publisherParams);
        session.publish(publisher);
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOGTAG, "call to onStreamReceived");
        Subscriber subscriber = new Subscriber(MainActivity.this, stream);
        subscriber.setVideoListener(this);
        session.subscribe(subscriber);
        subscriberView.addView(subscriber.getView(), subscriberParams);
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
