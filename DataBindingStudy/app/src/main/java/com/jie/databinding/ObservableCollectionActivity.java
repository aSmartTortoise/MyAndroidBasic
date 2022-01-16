package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableList;
import androidx.databinding.ObservableMap;

import android.os.Bundle;
import android.view.View;

import com.jie.databinding.databinding.ActivityObservableCollectionBinding;

import java.util.Random;

public class ObservableCollectionActivity extends AppCompatActivity {
    private ObservableMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityObservableCollectionBinding collectionBinding = DataBindingUtil.setContentView(this, R.layout.activity_observable_collection);
        map = new ObservableArrayMap<>();
        map.put("name", "frankie");
        map.put("age", "34");
        collectionBinding.setMap(map);
        ObservableList<String> list = new ObservableArrayList<>();
        list.add("frankie");
        list.add("34");
        collectionBinding.setList(list);
        collectionBinding.setIndex(1);
        collectionBinding.setKey("name");
    }

    public void onClick(View view) {
        map.put("name", "frankie" + new Random().nextInt(100));
    }
}