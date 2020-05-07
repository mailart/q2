package com.example.jim.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkConnected() ){
            triggerToast(getString(R.string.network_error));

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onClick(null);
                }
            }

        }
    }
    /*
    * check the network connection, internet permission and request permission if necessary
    * */
    private boolean statusCheck(){

        if(isNetworkConnected() == false){
            triggerToast(getString(R.string.network_error));

            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    1);

            triggerToast(getString(R.string.permission_error));
            return false;
            // Permission is not granted
        }
        return true;
    }
    /*
    check network
    * */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    /*
    event handler
    * */
    public void onClick(View v){
        String url ="https://capi.stage.9c9media.com/destinations/tsn_ios/platforms/iPad/contents/69585" ;
        if(statusCheck() ) {//check network status
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {//data returned
                            try {
                                JSONObject ret = new JSONObject(response);//convert to json
                                String lastModDate = ret.getString("LastModifiedDateTime");
                                alertMsg(formatTime(lastModDate));//convert format and popup
                            } catch (JSONException e) {
                                triggerToast(getString(R.string.data_error));
                                e.printStackTrace();
                            } catch (ParseException e) {
                                triggerToast(getString(R.string.date_error));
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    triggerToast(getString(R.string.server_error));
                }
            });

            queue.add(stringRequest);
        }
    }
    /*
    *convert utc date string to  date time format of the device
    * */
    private String formatTime(String date) throws ParseException {
        SimpleDateFormat utctolocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.getDefault());
        Date localtm;
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(this);
        utctolocal.setTimeZone(TimeZone.getTimeZone("GMT"));
        localtm = utctolocal.parse(date);
        utctolocal = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());

        return dateFormat.format(localtm) + " " + utctolocal.format(localtm);
    }

    /*
    * alert dialog function
    * */
    private void alertMsg(String msg){
        new android.app.AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Last Modified Date Time")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .show();

    }
    /*
        toast msg for error
    * */
    private void triggerToast(String msg){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }
}
