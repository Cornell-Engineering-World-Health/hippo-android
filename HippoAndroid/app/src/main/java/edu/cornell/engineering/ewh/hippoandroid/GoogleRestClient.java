package edu.cornell.engineering.ewh.hippoandroid;

import com.loopj.android.http.*;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Lillyan on 4/30/17.
 */

public class GoogleRestClient extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String[] params) {
        StringBuffer response = new StringBuffer();
        try {
            String url = "https://ewh-hippo.herokuapp.com/auth/google";
//            String url = "http://10.0.2.2:3000/auth/google";
            URL obj = new URL(url);
//            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
//            con.setDoOutput(false);

            //add request header
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
                System.out.println("error");
            }
            else {
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            }
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());

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
    }
}
