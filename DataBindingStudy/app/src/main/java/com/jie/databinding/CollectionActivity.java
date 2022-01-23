package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.SparseArray;

import com.jie.databinding.databinding.ActivityCollectionBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollectionBinding collectionBinding = DataBindingUtil.setContentView(this, R.layout.activity_collection);
        String[] strArr = new String[] {"Hi", "Hello"};
        List<String> list = new ArrayList<>();
        list.add("Hello");
        Map<String, String> map = new HashMap<>();
        map.put("jackie", "Hi");
        Set<String> set = new HashSet<>();
        set.add("Hi");
        SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.put(0, "Hello");
        collectionBinding.setIndex(0);
        collectionBinding.setKey("jackie");
        collectionBinding.setArray(strArr);
        collectionBinding.setList(list);
        collectionBinding.setSet(set);
        collectionBinding.setMap(map);
        collectionBinding.setSparse(sparseArray);
        setTitle("Collection in xml");
    }
}