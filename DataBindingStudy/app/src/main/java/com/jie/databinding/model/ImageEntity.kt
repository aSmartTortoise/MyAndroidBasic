package com.jie.databinding.model

import androidx.databinding.ObservableField

class ImageEntity(url: String) {
    var urlField: ObservableField<String>? = null
    init {
        urlField = ObservableField<String>(url)
    }
}