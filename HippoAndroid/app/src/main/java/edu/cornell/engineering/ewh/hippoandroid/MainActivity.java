package edu.cornell.engineering.ewh.hippoandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.StringBuilderPrinter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.widget.ListView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.widget.TextView;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

/*
* MainActivity controls the main Upcoming Session List view. 
* Refreshes the call list every minute, and creates the Intent to 
* the video call view on click of a call.
*/
public class MainActivity extends AppCompatActivity implements AsyncResponse {
    public static final String SESSION_NAME = "edu.cornell.engineering.ewh.hippoandroid.SESSION_NAME";
    public boolean mainActivityActive;
    AsyncCall getSessions;

    // handler changes session list view in main thread.
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            refresh();
        }
    };

    public GoogleApiClient mGoogleApiClient;

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

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTitle = (TextView) myToolbar.findViewById(R.id.toolbar_title);
        Typeface lato = Typeface.createFromAsset(this.getApplication().getAssets(), "fonts/Lato-Bold.ttf");
        mTitle.setTypeface(lato);

        // this to set delegate/listener back to this class
        getSessions.delegate = this;

        SharedPreferences sharedPreferences = this.getSharedPreferences("APP", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Authorization", "default, means there was no G_TOKEN");

        // execute the async task
        getSessions.execute("https://ewh-hippo.herokuapp.com/api/self", token);

        mainActivityActive = true; // true if user is on mainActivity.
          // new thread runs only while mainActivityActive == true
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
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                signOut();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("APP", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivityActive = false;
    }

    /**
     * Refreshes the Session List by making a GET request
     */
    public void refresh(){
        AsyncResponse del = getSessions.delegate;
        getSessions = new AsyncCall();
        getSessions.delegate = del;

        SharedPreferences sharedPreferences = this.getSharedPreferences("APP", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Authorization", "default, means there was no G_TOKEN");

        getSessions.execute("https://ewh-hippo.herokuapp.com/api/self", token);
    }

    /**
     * Convert date from UTC to local time.
     */
    public String convertDate(String date){
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(date);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatter.setTimeZone(TimeZone.getDefault());
            date = dateFormatter.format(value);
        } catch (Exception e) {
            date = "0000-00-00 00:00:00";
        }
        return date;
    }
    
    /** 
     * Generates upcoming call list, parsing out inactive calls.
     */
    public void processFinish(String output) {

        try{
            JSONObject jsonObject = new JSONObject(output);
            JSONArray calls = jsonObject.getJSONArray("calls");
            int myUserId = jsonObject.getInt("userId");

            final ArrayList<CallSession> sessions = new ArrayList<CallSession>();
            for(int i = 0; i<calls.length();i++){
                JSONObject call = calls.getJSONObject(i);
                
                // add to list if the call has not ended.
                if(call.getBoolean("active")){
                    JSONArray participants = call.getJSONArray("participants");
                    User[] users = new User[participants.length()-1];
                    int k = 0;
                    
                    // add all call participants except yourself.
                    for(int j = 0; j< participants.length(); j++){
                        JSONObject member = participants.getJSONObject(j);
                        int pUserId = member.getInt("userId");
                        if(myUserId != pUserId){
                            users[k++] = new User(pUserId, member.getString("email"),
                                    member.getString("lastName"), member.getString("firstName"), member.getString("calls"));
                        }
                    }

                    sessions.add(new CallSession(convertDate(call.getString("endTime")), convertDate(call.getString("startTime")),
                            convertDate(call.getString("datetime")), call.getString("sessionId"), call.getString("name"),
                            call.getBoolean("active"), users));

                }

            }
            try {
                SessionAdapter<CallSession> sessionsAdapter;
                sessionsAdapter = new SessionAdapter<CallSession>(getApplicationContext(), R.layout.session_item, sessions.toArray(new CallSession[sessions.size()]));

                ListView sessionListView = (ListView) findViewById(R.id.session_list);
                sessionListView.setAdapter(sessionsAdapter);

                sessionsAdapter.sort(new Comparator<CallSession>() {
                    @Override
                    public int compare(CallSession s1, CallSession s2) {
                        return s1.getStartTime().compareTo(s2.getStartTime());
                    }
                });
                
                // listens for user click on call
                sessionListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {
                        // get clicked Session.
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
