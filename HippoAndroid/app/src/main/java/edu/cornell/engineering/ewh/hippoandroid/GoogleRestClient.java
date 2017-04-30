package edu.cornell.engineering.ewh.hippoandroid;

import com.loopj.android.http.*;
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
//    private static final String BASE_URL = "https://ewh-hippo.herokuapp.com/";
//    private static AsyncHttpClient client = new AsyncHttpClient();
//
//    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.get(getAbsoluteUrl(url), params, responseHandler);
//    }
//
//    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.post(getAbsoluteUrl(url), params, responseHandler);
//    }
//
//    private static String getAbsoluteUrl(String relativeUrl) {
//        return BASE_URL + relativeUrl;
//    }
    private Exception exception;
    public String authCode = "";

    @Override
    protected String doInBackground(String[] params) {
        // GET all of user's sessions


        StringBuffer response = new StringBuffer();
        try {
            String url = "https://ewh-hippo.herokuapp.com/auth/google";
//            String url = "http://10.0.2.2:3000/auth/google";
            URL obj = new URL(url);
//            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
//            con.setDoOutput(false);

            //add reuqest header
            con.setRequestMethod("POST");
//            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            //Request Parameters you want to send
            String urlParameters = "code="+authCode+"&redirectUri=http://localhost:8080&clientId=707301966243-kbftfe8f7hb0hqv6eu7pkgg02gfhbhlc.apps.googleusercontent.com";

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
