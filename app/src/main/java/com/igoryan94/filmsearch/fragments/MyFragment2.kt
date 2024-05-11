package com.igoryan94.filmsearch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.igoryan94.filmsearch.databinding.FragmentMy2Binding

class MyFragment2 : Fragment() {
    lateinit var b: FragmentMy2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentMy2Binding.inflate(inflater, container, false)

        val input = arguments?.getString("input")
        b.textReceiver.text = input

        // Inflate the layout for this fragment
        return b.root
    }
}