package com.jie.databinding.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jie.databinding.R
import com.jie.databinding.model.UserEntity
import com.jie.databinding.UserAdapter
import kotlin.random.Random

class RecyclerViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        findViewById<RecyclerView>(R.id.rv).apply {
            layoutManager = LinearLayoutManager(this@RecyclerViewActivity)
            val users = mutableListOf<UserEntity>()
            for (i in 0..10) {
                users.add(UserEntity("user_$i", "${Random.nextInt()}"))
            }
            adapter = UserAdapter(users)
        }
        title = "recyclerView study"
    }
}