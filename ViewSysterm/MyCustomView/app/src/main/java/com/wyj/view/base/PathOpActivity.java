package com.wyj.view.base;

import android.graphics.PathMeasure;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.wyj.view.R;

public class PathOpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_op);
        PathMeasure pathMeasure = new PathMeasure();
    }
}