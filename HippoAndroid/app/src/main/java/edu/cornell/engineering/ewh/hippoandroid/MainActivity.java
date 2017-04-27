package edu.cornell.engineering.ewh.hippoandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.StringBuilderPrinter;
import android.widget.LinearLayout;
import android.util.Log;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.opentok.android.OpentokError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class MainActivity extends AppCompatActivity implements AsyncResponse {
    AsyncCall getSessions = new AsyncCall();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main: ", "call to onCreate");
        super.onCreate(savedInstanceState);

        LinearLayout parentLayout = new LinearLayout(this);
        setContentView(parentLayout);

        //this to set delegate/listener back to this class
        getSessions.delegate = this;

        //execute the async task
        getSessions.execute("https://ewh-hippo.herokuapp.com/api/self");
    }

    public void processFinish(String output) {

        try{
            JSONObject jsonObject = new JSONObject(output);
            JSONArray calls = jsonObject.getJSONArray("calls");
            CallSession[] sessions = new CallSession[calls.length()];
            for(int i = 0; i<calls.length();i++){
                JSONObject call = calls.getJSONObject(i);
                JSONArray participants = call.getJSONArray("participants");
                User[] users = new User[participants.length()];
                for(int j = 0; j< participants.length(); j++){
                    JSONObject member = participants.getJSONObject(j);
                    users[j] = new User(member.getString("userId"), member.getString("email"),
                            member.getString("lastName"), member.getString("firstName"), member.getString("calls"));
                }

                sessions[i] = new CallSession(call.getString("endTime"), call.getString("startTime"),
                        call.getString("datetime"), call.getString("sessionId"), call.getString("name"),
                        call.getBoolean("active"), users);

            }

            System.out.println(sessions);
        } catch(Exception e) {
            System.out.println("Error while processing JSON");
        }
        System.out.println("ProcessFinish: "+output);
    }

}