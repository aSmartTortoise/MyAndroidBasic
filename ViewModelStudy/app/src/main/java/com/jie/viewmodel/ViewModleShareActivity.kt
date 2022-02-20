package com.jie.viewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.jie.viewmodel.fragment.FragmentA
import com.jie.viewmodel.fragment.FragmentB

class ViewModleShareActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ViewModleShareActivity"
    }

    private val mViewClickListener: View.OnClickListener = View.OnClickListener {
        val  beginTransaction = supportFragmentManager.beginTransaction()
        when(it.id) {
            R.id.btn_showA -> {
                if (mFragmentA == null) {
                    mFragmentA = FragmentA()
                }
                Log.d(TAG, "wyj showA")
                beginTransaction.replace(R.id.fl_content, mFragmentA!!)
            }
            R.id.btn_showB -> {
                if (mFragmentB == null) {
                    mFragmentB = FragmentB()
                }
                Log.d(TAG, "wyj showB")
                beginTransaction.replace(R.id.fl_content, mFragmentB!!)
            }
        }

        beginTransaction.commitAllowingStateLoss()
    }

    private var mFragmentA: Fragment? = null
    private var mFragmentB: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_modle_share)
        findViewById<Button>(R.id.btn_showA).setOnClickListener(mViewClickListener)
        findViewById<Button>(R.id.btn_showB).setOnClickListener(mViewClickListener)
    }
}