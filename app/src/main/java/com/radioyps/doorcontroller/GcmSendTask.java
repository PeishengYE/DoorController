package com.radioyps.doorcontroller;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yep on 10/10/16.
 */

    public class GcmSendTask extends AsyncTask<String, Void, String> {

        private static final String LOG_TAG = "GcmSendTask";
        @Override
        protected String doInBackground(String... params) {


            final String API_KEY = BuildConfig.GCMAPIKEY; // An API key saved on the app server that gives the app server authorized access to Google services
//            final String Remote_GCM_TOKEN = "e_2eh3HclGQ:APA91bGm55kjkioHcKoY0NTq2xX1WL4mSkFuDtvOEz9QASsw23sYmfRSrRErJZuPGgCxX0z_m7wXne9f3YyRGQgREyr6U0nAD13vrBj5lkv8EXBEwcowih-dFO9KJzCJe7eLzqIfWwj0"; //An ID issued by the GCM connection servers to the client app that allows it to receive messages
            //final String Remote_GCM_TOKEN = "dTfcTU6yk6s:APA91bFXnAc-Vy_DjXIe09WURsyN-bewR3mfkbzRyBxyk1MBHKViTmvs30o-PJcDt3d9E3bRbadqfCj0LTOWBCqCtJXYTvmMUEPVuOE1IWNKcywM9XAd4bK8HaBHrRz-0wZJRdOFOO6y";
            /* FIXME this may be a very bad idea to coding like this */
            String Remote_GCM_TOKEN = MainActivity.getRemoteToken();
            final String postData = "{ \"registration_ids\": [ \"" + Remote_GCM_TOKEN + "\" ], " +
                   // "\"delay_while_idle\": true, " +
                    "\"data\": {\"tickerText\":\"My Ticket\", " +
                    "\"contentTitle\":\"My Title\", " +
                    "\"message\": \"";
            final String endData = "\"}}";
            StringBuilder toSend = new StringBuilder();
            long currentTime = System.currentTimeMillis();

            toSend.append(postData);
            toSend.append(params[0]);
            toSend.append("\", \"");
            toSend.append(CommonConstants.GCM_SENDING_TIME_KEY);
            toSend.append("\": \"");
            toSend.append(currentTime);
            toSend.append("\", \"");
            toSend.append(CommonConstants.GCM_SENDING_TOKEN_KEY);
            toSend.append("\": \"");
            String localToken = MainActivity.getLocalToken();
            toSend.append(localToken);

            toSend.append(endData);

            try {
                Log.i(LOG_TAG, "GCMRequest()>> sending <<" + toSend.toString() + ">>");
                URL url = new URL("https://gcm-http.googleapis.com/gcm/send");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "key=" + API_KEY);

                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                writer.write(toSend.toString());
                writer.flush();
                writer.close();
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                InputStream inputStream;
                if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                    inputStream = urlConnection.getInputStream();
                } else {
                    inputStream = urlConnection.getErrorStream();
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp, response = "";
                while ((temp = bufferedReader.readLine()) != null) {
                    response += temp;
                }
                Log.i(LOG_TAG, "GCMRequest()>> response: "+ response);
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }

        }

        protected void onPostExecute(String message) {
            super.onPostExecute(message);


                try {
                    JSONObject jsonObject = new JSONObject(message);
                    MainActivity.sendMessage(CommonConstants.MSG_GCM_CMD_STATUS, CommonConstants.FLAG_GCM_OK);
                } catch (JSONException e) {
                    e.printStackTrace();
                    MainActivity.sendMessage(CommonConstants.MSG_GCM_CMD_STATUS, CommonConstants.FLAG_GCM_FAILURE);
                }

        }
    }

