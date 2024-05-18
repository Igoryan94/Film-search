package com.igoryan94.filmsearch.fragments.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.activities.training.ImageViewTestActivity
import com.igoryan94.filmsearch.databinding.FragmentMyBinding

class MyFragment : Fragment() {
    lateinit var b: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentMyBinding.bind(inflater.inflate(R.layout.fragment_my, container, false))

        b.buttonSend.setOnClickListener {
            (activity as ImageViewTestActivity).passData(b.editText.text?.toString() ?: "")
        }

        // Inflate the layout for this fragment
        return b.root
    }
}