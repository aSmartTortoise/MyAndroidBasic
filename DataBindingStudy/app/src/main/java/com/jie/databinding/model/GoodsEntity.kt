package com.jie.databinding.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.jie.databinding.BR

class GoodsEntity : BaseObservable() {
    @Bindable
    var name: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @Bindable
    var detail: String? = null
        set(value) {
            field = value
            notifyChange()
        }

    var price: Float = 0.0f
}
