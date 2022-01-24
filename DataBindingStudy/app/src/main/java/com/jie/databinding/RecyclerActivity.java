package com.jie.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.jie.databinding.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecyclerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        RecyclerView rv = findViewById(R.id.rv);
        List<User> users = new ArrayList<>();
        User user;
        for (int i = 0; i < 10; i++) {
            user = new User("user_" + i, new Random().nextInt() + "");
            users.add(user);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        UserAdapter adapter = new UserAdapter(users);
        rv.setAdapter(adapter);
        setTitle("recyclerView study");
    }
}