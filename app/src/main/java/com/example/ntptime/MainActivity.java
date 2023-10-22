package com.example.ntptime;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
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

        handler = new Handler();

        // Update tid when starting
        getSystemTime();

        // Update every sec
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    getNetworkTime();
                } else {
                    getSystemTime();
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    // Function to get system time
    private void getSystemTime() {
        Date date = new Date(System.currentTimeMillis());
        String time = timeFormat.format(date);
        timeTextView.setText("System time: " + time);
        timeTextView.setTextColor(Color.parseColor("#FF33FF"));
    }

    // Function to get network time
    private void getNetworkTime() {
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
                        timeTextView.setTextColor(Color.parseColor("#0000FF"));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkTimeThread.start();
    }

    //Function for checking network availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            return false;
        }
    }
}
