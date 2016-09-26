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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static int connectPort = 5028;
    private static String OPEN_THE_DOOR = "A412..&35?@!";

    private final static int SOCKET_TIMEOUT = 10 * 000; /*10 seconds */
    private final static String IP_ADDR = "192.168.12.238";
    private final static String NETWORK_ERROR = "network error";
    private static boolean isCmdFinished = true;
    private static Context mContext = null;

    private final static int RESULT_SUCCESS = 0x11;
    private final static int RESULT_IO_ERROR = 0x12;
    private final static int RESULT_TIMEOUT = 0x13;

    private final static int RESULT_HOST_UNAVAILABLE = 0x14;
    private final static int RESULT_HOST_REFUSED = 0x15;
    private final static int RESULT_NETWORK_UNREACHABLE = 0x16;
    private final static int RESULT_HOSTNAME_NOT_FOUND = 0x17;
    private final static int RESULT_UNKNOWN = 0x18;

    private final static String  EXCEPTION_NETWORK_UNREACHABLE = "ENETUNREACH";
    private final static String  EXCEPTION_HOST_UNAVAILABLE = "EHOSTUNREACH";
    private final static String  EXCEPTION_HOST_REFUSED = "ECONNREFUSED";
    private static int response = RESULT_UNKNOWN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
		/*
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
		*/
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


		private String getStatusString(int status){
			String ret = getString(R.string.result_cmd_unknown);
			switch(status){
                 case RESULT_HOST_REFUSED:
                     ret = getString(R.string.result_cmd_host_refused);
                     break;
                case RESULT_HOST_UNAVAILABLE:
                    ret = getString(R.string.result_cmd_host_unavailable);
                    break;
                case RESULT_HOSTNAME_NOT_FOUND:
                    ret = getString(R.string.result_cmd_hostname_not_found);
                    break;
                case RESULT_IO_ERROR:
                    ret = getString(R.string.result_cmd_io_error);
                    break;
                case RESULT_NETWORK_UNREACHABLE:
                    ret = getString(R.string.result_cmd_network_unreachable);
                    break;
                case RESULT_TIMEOUT:
                    ret = getString(R.string.result_cmd_timeout);
                    break;
                case RESULT_SUCCESS:
                    ret = getString(R.string.result_cmd_success);
                    break;
                default:
                    ret = getString(R.string.result_cmd_unknown);



            }
            return ret;

		}

		private int getConnectionErrorCode(String error){
			int ret = RESULT_UNKNOWN;

			if(error.indexOf(EXCEPTION_NETWORK_UNREACHABLE) != -1){
                  ret =  RESULT_NETWORK_UNREACHABLE;
			}else if(error.indexOf(EXCEPTION_HOST_UNAVAILABLE) != -1){
                  ret =  RESULT_HOST_UNAVAILABLE;
			}else if(error.indexOf(EXCEPTION_HOST_REFUSED) != -1){
                  ret = RESULT_HOST_REFUSED;
			}
            return ret;

		}
		private String getConnectionError(ConnectException ex){


			String ret = null;

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionStr = sw.toString(); 
			String lines[] = exceptionStr.split("\\r?\\n");
			for(int i = 0; i < lines.length; i++){
				 if(lines[i].indexOf("ConnectException") != -1){
					 ret = lines[i];
					 break;
				 }
			 }


            Log.d(TAG, "getConnectionError()>> Line: " + ret);
			return ret;

		}


        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            String errorStr = getStatusString(response);
            Log.d(TAG, "toggleDoor()>> status of cmd: " + errorStr);
            Toast.makeText(mContext,
                    errorStr, Toast.LENGTH_LONG).show();
        }



        @Override
        protected String[] doInBackground(String... paramsNot) {


            Socket socket = null;
            String stringReceived = "";


            try {

                response = RESULT_UNKNOWN;

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


                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    stringReceived += byteArrayOutputStream.toString("UTF-8");
                }
                outputStream.close();
                inputStream.close();
                response = RESULT_SUCCESS;

            } catch (ConnectException e) {
                e.printStackTrace();
		        String errorStr = getConnectionError(e);
                if(errorStr != null)
				response =  getConnectionErrorCode(errorStr);
                else
                    response = RESULT_UNKNOWN;

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = RESULT_HOSTNAME_NOT_FOUND ;

            } catch (SocketTimeoutException e) {

                e.printStackTrace();
                response = RESULT_TIMEOUT;

            } catch (IOException e) {
                e.printStackTrace();
                response = RESULT_IO_ERROR;


            } finally {
                isCmdFinished = true;
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
