package com.jie.livedata.global

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.jie.livedata.R
import com.jie.livedata.databinding.FragmentBBinding

class FragmentB : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentABinding = DataBindingUtil.inflate<FragmentBBinding>(
            inflater,
            R.layout.fragment_b,
            container,
            false
        )
        GlobalLiveData.getInstance().observe(
            viewLifecycleOwner
        ) { fragmentABinding.tvFragmentB.text = "fragmentB:$it" }
        return fragmentABinding.root
    }

}