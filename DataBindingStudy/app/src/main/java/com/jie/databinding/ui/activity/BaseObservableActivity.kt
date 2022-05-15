package com.jie.databinding.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import com.jie.databinding.BR
import com.jie.databinding.R
import com.jie.databinding.databinding.ActivityBaseobservableBinding
import com.jie.databinding.model.GoodsEntity
import java.util.*

class BaseObservableActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BaseObservableActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val baseobservableBinding = DataBindingUtil.setContentView<ActivityBaseobservableBinding>(
            this,
            R.layout.activity_baseobservable
        )
        val goods: GoodsEntity = GoodsEntity()
        goods.name = "Doge Coin"
        goods.detail = "Buy something."
        goods.price = 0.25f
        baseobservableBinding.goods = goods
        baseobservableBinding.goodsHandler = GoodsHandler(goods)
        title = "BaseObservable Study"
        goods.addOnPropertyChangedCallback (object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                when(propertyId) {
                    BR.name -> Log.d(TAG, "onPropertyChanged: wyj goods name changed")
                    BR.detail -> Log.d(TAG, "onPropertyChanged: wyj goods detail changed")
                    BR._all -> Log.d(TAG, "onPropertyChanged: wyj goods all property changed")
                    else -> Log.d(TAG, "onPropertyChanged: wyj unknow.")
                }
            }
        })
    }

    inner class GoodsHandler(val goods: GoodsEntity) {
        fun changeGoodsName() {
            goods.name = "dogeCoin super ${Random().nextInt(100)}"
            goods.price = Random().nextFloat()
        }

        fun changeGoodsDetails() {
            goods.detail = "in 2022, tesla goods can be buy with dogeCoin, said Elon Musk. ${Random().nextInt(100)}"
            goods.price = Random().nextFloat()
        }
    }
}