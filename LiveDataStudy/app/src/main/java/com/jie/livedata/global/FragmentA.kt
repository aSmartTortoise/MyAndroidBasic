package com.jie.livedata.global

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jie.livedata.R
import com.jie.livedata.databinding.FragmentABinding


class FragmentA : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentABinding = DataBindingUtil.inflate<FragmentABinding>(
            inflater,
            R.layout.fragment_a,
            container,
            false
        )
        GlobalLiveData.getInstance().observe(
            viewLifecycleOwner
        ) { fragmentABinding.tvFragmentA.text = "fragmentA:$it" }
        return fragmentABinding.root
    }

}