package com.jie.viewmodel.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.jie.viewmodel.R
import com.jie.viewmodel.ShareDataModel

class FragmentB : Fragment() {
    val mViewModel: ShareDataModel by activityViewModels<ShareDataModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_b, container, false).apply {
            mViewModel.mLiveData.observe(viewLifecycleOwner) {
                value -> findViewById<TextView>(R.id.tv_B).text = "FragmentB:$value"
            }
        }
    }

}