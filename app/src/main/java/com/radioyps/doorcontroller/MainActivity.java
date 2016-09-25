package com.radioyps.doorcontroller;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static int connectPort = 5028;
    private static String OPEN_THE_DOOR = "A412..&35?@!";
    private static String response = null;
    private final static int SOCKET_TIMEOUT = 10 * 000; /*10 seconds */
    private final static String IP_ADDR = "192.168.12.238";
    private final static String NETWORK_ERROR = "network error";
    private static boolean isCmdFinished = true;
    private static Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggleDoor(View view) {

        Log.d(TAG, "toggleDoor()>> ");
        String cmd = "hello";
        if(isCmdFinished){
            isCmdFinished = false;
            Log.d(TAG, "toggleDoor()>> ok making a task ");
            Toast.makeText(this,
                    getString(R.string.cmd_in_progress), Toast.LENGTH_LONG).show();
            sendCmdOverTcpTask sendTask = new sendCmdOverTcpTask();
            sendTask.execute(cmd);

        }else{
            Log.d(TAG, "toggleDoor()>> previous cmd unfinished, give up ");
            Toast.makeText(this,
                    getString(R.string.prv_cmd_in_progress_give_up), Toast.LENGTH_LONG).show();
        }


    }


    public class sendCmdOverTcpTask extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = sendCmdOverTcpTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            Toast.makeText(mContext,
                    getString(R.string.success_on_cmd), Toast.LENGTH_LONG).show();
            Log.d(TAG, "toggleDoor()>> response: " + response);
        }



        @Override
        protected String[] doInBackground(String... paramsNot) {


            Socket socket = null;



            try {

                response = "";

                socket = new Socket(IP_ADDR, connectPort);
                socket.setSoTimeout(SOCKET_TIMEOUT);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);

                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();


                outputStream.write(OPEN_THE_DOOR.getBytes());
                outputStream.flush();

//                Toast.makeText(mContext,
//                        "Sending String ", Toast.LENGTH_LONG).show();
			/*
			 * notice: inputStream.read() will block if no data return
			 */

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                outputStream.close();
                inputStream.close();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();

//                Toast.makeText(mContext,
//                        "UnknownHostException: ", Toast.LENGTH_LONG).show();
            } catch (SocketTimeoutException e) {

                Log.i(LOG_TAG, "sendCmdOverTcpTask()>> exception on TIMEOUT error ");
//                Toast.makeText(mContext,
//                        "TIMEOUT error ", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                response = NETWORK_ERROR;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //response = "IOException: " + e.toString();
                response = NETWORK_ERROR;
//                Toast.makeText(mContext,
//                        "NETWORK ERROR", Toast.LENGTH_LONG).show();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        isCmdFinished = true;
//                        Toast.makeText(mContext,
//                                "Close Socket", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
