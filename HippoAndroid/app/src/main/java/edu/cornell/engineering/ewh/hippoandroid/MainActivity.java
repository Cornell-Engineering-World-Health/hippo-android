package edu.cornell.engineering.ewh.hippoandroid;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.StringBuilderPrinter;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.widget.ListView;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements AsyncResponse {
    public static final String SESSION_NAME = "edu.cornell.engineering.ewh.hippoandroid.SESSION_NAME";
    public boolean mainActivityActive;
    AsyncCall getSessions;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main: ", "call to onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSessions = new AsyncCall();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //this to set delegate/listener back to this class
        getSessions.delegate = this;

        //execute the async task
        getSessions.execute("https://ewh-hippo.herokuapp.com/api/self");

        mainActivityActive = true; //true if user is on mainActivity.
        //new thread runs only while mainActivityActive == true
        Runnable r = new Runnable(){
            @Override
            public void run() {
                while(mainActivityActive){
                    synchronized (this){
                        try{
                            wait(60000);
                            handler.sendEmptyMessage(0);
                        } catch (Exception e){
                            Log.d("Main: ", "Error in refresh thread: " + e);
                        }
                    }
                }
            }
        };
        Thread refresh = new Thread(r);
        refresh.start();
    }

    @Override

    public void onPause() {
        super.onPause();
        mainActivityActive = false;
    }
    public void refresh(){
        System.out.println("Refresh");
        AsyncResponse del = getSessions.delegate;
        getSessions = new AsyncCall();
        getSessions.delegate = del;
        getSessions.execute("https://ewh-hippo.herokuapp.com/api/self");
    }

    public void processFinish(String output) {

        try{
            JSONObject jsonObject = new JSONObject(output);
            JSONArray calls = jsonObject.getJSONArray("calls");
            int myUserId = jsonObject.getInt("userId");

            final List<CallSession> sessions = new ArrayList<>();

            for(int i = 0; i<calls.length();i++){
                JSONObject call = calls.getJSONObject(i);
                //add to list if call has not ended.
                if(call.getBoolean("active")){
                    JSONArray participants = call.getJSONArray("participants");
                    User[] users = new User[participants.length()-1];
                    int k = 0;
                    //add participants except self.
                    for(int j = 0; j< participants.length(); j++){
                        JSONObject member = participants.getJSONObject(j);
                        int pUserId = member.getInt("userId");
                        if(myUserId != pUserId){
                            users[k++] = new User(pUserId, member.getString("email"),
                                    member.getString("lastName"), member.getString("firstName"), member.getString("calls"));
                        }
                    }

                    sessions.add(new CallSession(call.getString("endTime"), call.getString("startTime"),
                            call.getString("datetime"), call.getString("sessionId"), call.getString("name"),
                            call.getBoolean("active"), users));
                }
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
                        CallSession call = sessions.get(position);
                        Intent intent = new Intent(MainActivity.this, VideoCallActivity.class);
                        intent.putExtra(SESSION_NAME, call.getName());
                        startActivity(intent);
                    }
                });

            }
            catch(Exception e) {
                Log.d("processFinish", "Error while creating list view: " + e);
            }
        } catch(Exception e) {
            Log.d("processFinish", "Error while processing JSON: "+e.getMessage());
        }


    }

}