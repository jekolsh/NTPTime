package com.example.ntptime;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView systemTimeTextView;
    private TextView networkTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the TextView elements in the layout via ID
        systemTimeTextView = findViewById(R.id.systemTimeTextView);
        networkTimeTextView = findViewById(R.id.networkTimeTextView);

        systemTimeTextView.setText("Systemtid: " + getSystemTime());
        networkTimeTextView.setText("Nätverkstid: " + getNetworkTime());
    }

    // function for pick up system time
    private String getSystemTime() {
        return "12:34:56";
    }

    // function for pick up network time
    private String getNetworkTime() {
        return "13:45:58";
    }

}