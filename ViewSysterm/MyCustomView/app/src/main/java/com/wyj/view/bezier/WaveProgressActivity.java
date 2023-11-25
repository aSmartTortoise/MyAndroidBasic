package com.wyj.view.bezier;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wyj.view.R;

public class WaveProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_progress);
    }

    public void onClick(View view){
        WaveProgressView waveView = (WaveProgressView)findViewById(R.id.wave);
        switch (view.getId()){
            case R.id.btn_begin:
                waveView.setMyProgress(0.6f);
                waveView.startAnim();
                break;
            default:
                break;
        }

    }
}