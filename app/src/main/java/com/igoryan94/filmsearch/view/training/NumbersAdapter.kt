package com.igoryan94.filmsearch.view.training

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class NumbersAdapter : ListDelegationAdapter<List<Any>>() {
    init {
        delegatesManager.addDelegate(NumberDelegateAdapter())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setItems(items: List<Any>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }

    fun submitList(list: List<Int>) {
        val diffCallback = NumberDelegateAdapter.NumberDiffCallback(items, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
    }
}