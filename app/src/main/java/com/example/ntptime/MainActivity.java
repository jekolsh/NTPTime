package com.example.ntptime;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

        // Initialize SimpleDateFormat for time formatting
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeTextView = findViewById(R.id.timeTextView);

        // Initialize a Handler to run tasks on the main (UI) thread
        handler = new Handler(Looper.getMainLooper());

        // Update time when the activity starts
        getSystemTime();

        // Periodically update the time every second
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    getNetworkTime();
                } else {
                    getSystemTime();
                }
                handler.postDelayed(this, 1000); // Schedule the Runnable again in 1 second
            }
        };

        handler.postDelayed(updateRunnable, 1000);
    }


    // Function to get and display the system time
    private void getSystemTime() {
        // Get the current date and time
        Date date = new Date(System.currentTimeMillis());
        // Format the current time using the timeFormat SimpleDateFormat
        String time = timeFormat.format(date);
        String formattedTime = getString(R.string.system_time_label, time);
        timeTextView.setText(formattedTime);
        timeTextView.setTextColor(Color.parseColor("#FF33FF"));
    }

    // Function to get and display network time
    private void getNetworkTime() {
        // Create a new NTPUDPClient to fetch network time
        NTPUDPClient client = new NTPUDPClient();
        // Create a new thread to run network time retrieval
        Thread networkTimeThread = new Thread(() -> {
            try {
                // Get the InetAddress of the NTP server
                InetAddress addr = InetAddress.getByName("time.google.com");
                TimeInfo info = client.getTime(addr);

                long networkTime = info.getMessage().getTransmitTimeStamp().getTime();
                Date adjustedTime = new Date(networkTime);
                String time = timeFormat.format(adjustedTime);

                runOnUiThread(() -> {
                    // Create a formatted time string with a label and update the UI on the main (UI) thread
                    String formattedTime = getString(R.string.network_time_label, time);
                    timeTextView.setText(formattedTime);
                    timeTextView.setTextColor(Color.parseColor("#0000FF"));
                });
            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(this::getSystemTime);
            }
        });
        networkTimeThread.start();
    }

    // Function to check network availability
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
