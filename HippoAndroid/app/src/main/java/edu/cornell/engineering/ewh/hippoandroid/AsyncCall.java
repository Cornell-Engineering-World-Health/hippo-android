package edu.cornell.engineering.ewh.hippoandroid;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by erinchen on 4/26/17.
 */

public class AsyncCall extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(String[] params) {
        // GET all of user's sessions
        Log.i("getMyCalls:", "[doInBackground] params = "+params[0]);

        StringBuffer response = new StringBuffer();

        try {
            URL obj = new URL(params[0]);
            String token = params[1];
            HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", token);
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
        System.out.println("onPostExecute: Got message!");
        delegate.processFinish(message);
    }
}