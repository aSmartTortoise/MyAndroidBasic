package com.jie.databinding.model

import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat

class ObservableGoodsEntity(name: String, details: String, price: Float) {
    var name: ObservableField<String>? = null
    var details: ObservableField<String>? = null
    var price: ObservableFloat? = null

    init {
        this.name = ObservableField(name)
        this.details = ObservableField(details)
        this.price = ObservableFloat(price)
    }
}
