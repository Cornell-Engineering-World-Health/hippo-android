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
 * Module that executes any HTTP call asynchronously.
 * Used with AsyncResponse to retrieve call result.
 */

public class AsyncCall extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(String[] params) {

        StringBuffer response = new StringBuffer();

        try {
            URL obj = new URL(params[0]);
            String token = params[1];
            HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", token);
            int responseCode = httpConnection.getResponseCode();
            Log.i("AsyncCall", "[doInBackground] response code = "+responseCode);
            
            if (responseCode == 200) {
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                        httpConnection.getInputStream()));

                String responseLine;

                while ((responseLine = responseReader.readLine()) != null) {
                    response.append(responseLine+"\n");
                }
                responseReader.close();
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
        // process message
        // System.out.println("onPostExecute: Got message!");
        delegate.processFinish(message);
    }
}
