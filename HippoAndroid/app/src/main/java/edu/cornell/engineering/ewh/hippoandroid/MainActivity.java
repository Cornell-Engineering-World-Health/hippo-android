package edu.cornell.engineering.ewh.hippoandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.widget.ListView;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements AsyncResponse {
    public static final String SESSION_NAME = "edu.cornell.engineering.ewh.hippoandroid.SESSION_NAME";

    AsyncCall getSessions = new AsyncCall();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main: ", "call to onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //this to set delegate/listener back to this class
        getSessions.delegate = this;

        //execute the async task
        getSessions.execute("https://ewh-hippo.herokuapp.com/api/self");
    }

    public void processFinish(String output) {

        try{
            JSONObject jsonObject = new JSONObject(output);
            JSONArray calls = jsonObject.getJSONArray("calls");
            final CallSession[] sessions = new CallSession[calls.length()];

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
                        Intent intent = new Intent(MainActivity.this, VideoCallActivityTemp.class);
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


    }

}