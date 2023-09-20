package com.wyj.performance.anr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.wyj.performance.R;

public class InputEventTimeoutActivity extends AppCompatActivity {
    private static final String TAG = "InputEventTimeout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_event_timeout);
        findViewById(R.id.btn).setOnClickListener(view -> {
            Log.d(TAG, "onCreate: wyj start sleep.");
            SystemClock.sleep(7_000L);
            Log.d(TAG, "onCreate: wyj sleep finish.");
        });
    }
}