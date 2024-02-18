package com.wyj.view.base;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.wyj.view.R;

public class PathApiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_study);
        ClockView clockView = findViewById(R.id.clock_view);
        clockView.start(60_000);
    }
}