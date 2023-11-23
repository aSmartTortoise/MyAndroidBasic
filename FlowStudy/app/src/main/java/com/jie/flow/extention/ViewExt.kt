package com.jie.flow.extention

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter

fun EditText.textWatcherCallbackFlow() = callbackFlow {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { trySend(it.toString()) }
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }
    addTextChangedListener(textWatcher)
    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}
    .filter { it.isNotEmpty() }
    .debounce(300L)

fun View.clickCallbackFlow(editText: EditText) = callbackFlow {
    setOnClickListener {
        editText.text.trim().toString().let {
            if (!TextUtils.isEmpty(it))
                trySend(it)
        }
    }
    awaitClose {
        setOnClickListener(null)
    }
}
    .filter { it.length > 2 }
    .throttleFirst(300L)