package com.wyj.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wyj.view.base.PathStudyActivity;
import com.wyj.view.timeline.TimeLineActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_time_line).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TimeLineActivity.class);
            MainActivity.this.startActivity(intent);
        });
        findViewById(R.id.btn_path).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PathStudyActivity.class);
            MainActivity.this.startActivity(intent);
        });
    }
}