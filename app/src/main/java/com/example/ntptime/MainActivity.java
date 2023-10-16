package com.example.ntptime;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView timeTextView;
    private SimpleDateFormat timeFormat;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeTextView = findViewById(R.id.timeTextView);

        /* Find the TextView elements in the layout via ID
        systemTimeTextView = findViewById(R.id.systemTimeTextView);
        networkTimeTextView = findViewById(R.id.networkTimeTextView);
         */

        handler = new Handler();

        // Update time when starting
        //updateNetworkTime();

        getSystemTime();

        //Update every sec
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNetworkAvailable()){
                    updateNetworkTime();
                } else {
                    getSystemTime();
                }
                handler.postDelayed(this, 1000); // Update every sec
            }
        }, 1000);
    }

    // function for pick up system time
    private String getSystemTime() {
        Date date = new Date(System.currentTimeMillis());
        String time = timeFormat.format(date);
        timeTextView.setText("System Time: " + time);
        timeTextView.setTextColor(Color.BLACK);
        return time;
    }



    // function for pick up network time
    private void updateNetworkTime() {
        NTPUDPClient client = new NTPUDPClient();
        Thread networkTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress addr = InetAddress.getByName("time.google.com");
                    client.open();
                    TimeInfo info = client.getTime(addr);
                    client.close();

                    Date networkTime = new Date(info.getReturnTime());
                    String time = timeFormat.format(networkTime);

                    runOnUiThread(() -> {
                        timeTextView.setText("Network Time: " + time);
                        timeTextView.setTextColor(Color.BLACK);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkTimeThread.start();
    }

    //function control over network
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null &&networkInfo.isConnected();
        } else{
            return false;
        }

    }
}