package com.wyj.view.base;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.wyj.view.R;

public class PathMeasureApiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_measure_api);
        ArrowMoveView arrowMoveView = findViewById(R.id.arrow_view);
        if (arrowMoveView != null) {
            arrowMoveView.startLoading();
        }
    }
}