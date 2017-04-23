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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity  {

    private LinearLayout publisherView;
    private LinearLayout.LayoutParams publisherParams;
    private LinearLayout subscriberView;
    private LinearLayout.LayoutParams subscriberParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main: ", "call to onCreate");
        super.onCreate(savedInstanceState);

        LinearLayout parentLayout = new LinearLayout(this);
        setContentView(parentLayout);

        new getMyCalls().execute("https://ewh-hippo.herokuapp.com/api/self");
    }

    private class getMyCalls extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            // GET all of user's sessions
            Log.i("getMyCalls:", "[doInBackground] params = "+params[0]);

            StringBuffer response = new StringBuffer();

            try {
                URL obj = new URL(params[0]);
                HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Authorization", " Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1OGQ2ZmUyODFmY2RlZjAwMTFmNTU0ZjQiLCJpYXQiOjE0OTI5ODI4MTIsImV4cCI6MTQ5NDE5MjQxMn0.3scDrs3eVW53V_zboesXb2tgiyDmlvw98I8TVFgeQaY");
                int responseCode = httpConnection.getResponseCode();
                Log.i("getMyCalls:", ""+responseCode);
                if (responseCode == 200) {
                    Log.i("getMyCalls", "[doInBackground] response code = "+responseCode);

                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                            httpConnection.getInputStream()));

                    String responseLine;

                    while ((responseLine = responseReader.readLine()) != null) {
                        response.append(responseLine+"\n");
                    }
                    responseReader.close();

                    // print result
                    Log.i("allSessions: ", response.toString());
                }
            }
            catch (MalformedURLException e) {
                Log.d("malformed URL: ", e.getMessage());
            }
            catch (IOException e) {
                Log.d("IOException", e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
        }
    }

}