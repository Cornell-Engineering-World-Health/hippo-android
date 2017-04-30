package edu.cornell.engineering.ewh.hippoandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
<<<<<<< HEAD
import android.support.v7.widget.Toolbar;
import android.util.StringBuilderPrinter;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.widget.ListView;
import android.content.Intent;
=======
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
    public static final String SESSION_ID = "2_MX40NTgxNzczMn5-MTQ5MjcxODEzMDU1NH56TklFTG1GU0dXSmVWTkZINWhBK3JvdVZ-UH4";
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NTgxNzczMiZzaWc9YzgzNzZhMzU2NzEzMDNkYWUzNWM1NTdiZjY4YzRhMWMzYmI1NzJiMDpzZXNzaW9uX2lkPTJfTVg0ME5UZ3hOemN6TW41LU1UUTVNamN4T0RFek1EVTFOSDU2VGtsRlRHMUdVMGRYU21WV1RrWklOV2hCSzNKdmRWWi1VSDQmY3JlYXRlX3RpbWU9MTQ5MjcxODE4NSZub25jZT0wLjA1Mzc2ODk3MjcwODQwMjk3JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTE0OTI4MDQ1ODU=";
    public static final String LOGTAG = "MainActivity";

    private LinearLayout publisherView;
    private LinearLayout.LayoutParams publisherParams;
    private LinearLayout subscriberView;
    private LinearLayout.LayoutParams subscriberParams;
>>>>>>> efd795ede345fba5cd66aee52be5d15ebd5f5025

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements AsyncResponse {
    public static final String SESSION_NAME = "edu.cornell.engineering.ewh.hippoandroid.SESSION_NAME";

    AsyncCall getSessions = new AsyncCall();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, "call to onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

<<<<<<< HEAD
        //this to set delegate/listener back to this class
        getSessions.delegate = this;

        //execute the async task
        getSessions.execute("https://ewh-hippo.herokuapp.com/api/self");
    }

    public void processFinish(String output) {

        try{
            JSONObject jsonObject = new JSONObject(output);
            JSONArray calls = jsonObject.getJSONArray("calls");
            int myUserId = jsonObject.getInt("userId");

            final CallSession[] sessions = new CallSession[calls.length()];

            for(int i = 0; i<calls.length();i++){
                JSONObject call = calls.getJSONObject(i);
                JSONArray participants = call.getJSONArray("participants");
                User[] users = new User[participants.length()-1];
                int k = 0;
                for(int j = 0; j< participants.length(); j++){
                    JSONObject member = participants.getJSONObject(j);
                    int pUserId = member.getInt("userId");
                    if(myUserId != pUserId){
                        users[k++] = new User(pUserId, member.getString("email"),
                                member.getString("lastName"), member.getString("firstName"), member.getString("calls"));
                    }
                }

                sessions[i] = new CallSession(call.getString("endTime"), call.getString("startTime"),
                        call.getString("datetime"), call.getString("sessionId"), call.getString("name"),
                        call.getBoolean("active"), users);

            }

            try {
                ArrayAdapter<CallSession> sessionsAdapter;
                sessionsAdapter = new ArrayAdapter<CallSession>(getApplicationContext(), R.layout.session_item, sessions);

                ListView sessionListView = (ListView) findViewById(R.id.session_list);
                sessionListView.setAdapter(sessionsAdapter);
                sessionListView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {
                        // Get clicked Session.
                        CallSession call = sessions[position];
                        Intent intent = new Intent(MainActivity.this, VideoCallActivity.class);
                        intent.putExtra(SESSION_NAME, call.getName());
                        startActivity(intent);
                    }
                });

            }
            catch(Exception e) {
                Log.d("processFinish", "Error while creating list view: "+e);
            }

        } catch(Exception e) {
            Log.d("processFinish", "Error while processing JSON: "+e.getMessage());
        }


=======
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
>>>>>>> efd795ede345fba5cd66aee52be5d15ebd5f5025
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
