package edu.cornell.engineering.ewh.hippoandroid;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lillyan on 4/30/17.
 * Asynchronous REST call to authenticate with the Hippo Backend
 */

public class GoogleRestClient extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(String[] params) {
        StringBuffer response = new StringBuffer();
        try {
            String url = "https://ewh-hippo.herokuapp.com/auth/google";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");

            //Add request header
            con.setRequestMethod("POST");

            //Request Parameters you want to send
            String clientId = params[0];
            String authCode = params[1];
            String urlParameters = "code="+authCode+"&redirectUri=http://localhost:8080&clientId=" + clientId;

            // Send post request
            con.setDoOutput(true);// Should be part of code only for .Net web-services else no need for PHP
            OutputStream wr = con.getOutputStream();
            wr.write(urlParameters.getBytes("UTF-8"));
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader in;
            if (responseCode > 400) {
                con.getErrorStream();
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            }
            else {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            }
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
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
        try {
            // Extract the auth token from the JSON response and call processFinish callback
            JSONObject jsonMessage = new JSONObject(message);
            String token = " Bearer " + jsonMessage.getString("token");
            delegate.processFinish(token);
        } catch(JSONException je) {
            System.out.println("FAILED TO PARSE MESSAGE FROM SERVER");
        }
    }
}
