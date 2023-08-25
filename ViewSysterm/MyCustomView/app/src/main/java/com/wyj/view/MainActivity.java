package com.wyj.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wyj.view.animation.ShoppingActivity;
import com.wyj.view.base.CanvasApiActivity;
import com.wyj.view.base.PathApiActivity;
import com.wyj.view.base.PathFillTypeActivity;
import com.wyj.view.base.PathMeasureApiActivity;
import com.wyj.view.base.PathOpActivity;
import com.wyj.view.bezier.BezierCurveActivity;
import com.wyj.view.bezier.WaveProgressActivity;
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
            Intent intent = new Intent(MainActivity.this, PathApiActivity.class);
            MainActivity.this.startActivity(intent);
        });
        findViewById(R.id.btn_bezier).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BezierCurveActivity.class);
            MainActivity.this.startActivity(intent);
        });
        findViewById(R.id.btn_fill_type).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PathFillTypeActivity.class);
            MainActivity.this.startActivity(intent);
        });
        findViewById(R.id.btn_op).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PathOpActivity.class);
            MainActivity.this.startActivity(intent);
        });
        findViewById(R.id.btn_pathMeasure_api).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PathMeasureApiActivity.class);
            MainActivity.this.startActivity(intent);
        });
        findViewById(R.id.btn_shopping).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ShoppingActivity.class);
            MainActivity.this.startActivity(intent);
        });
        findViewById(R.id.btn_wave_progress).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WaveProgressActivity.class);
            MainActivity.this.startActivity(intent);
        });
        findViewById(R.id.btn_canvas_api).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CanvasApiActivity.class);
            MainActivity.this.startActivity(intent);
        });
    }
}