package com.jie.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.jie.flow.extention.clickCallbackFlow
import com.jie.flow.extention.textWatcherCallbackFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CheeseActivity : BaseSearchActivity() {

    companion object {
        const val TAG = "CheeseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textWatcherCallbackFlow = dataBinding.queryEditText.textWatcherCallbackFlow()
        val clickCallbackFlow =
            dataBinding.searchButton.clickCallbackFlow(dataBinding.queryEditText)
        listOf(clickCallbackFlow, textWatcherCallbackFlow).merge()
            .onEach {
                dataBinding.progressBar.visibility = View.VISIBLE
            }
            .flowOn(Dispatchers.Main.immediate)
            .map { cheeseSearchEngine.search(it) }
            .flowOn(Dispatchers.IO)
            .onEach {
                Log.d(TAG, "onCreate: wyj it:$it")
                dataBinding.progressBar.visibility = View.GONE
                cheeseAdapter.setCheese(it)
            }
            .launchIn(lifecycleScope)
    }


}