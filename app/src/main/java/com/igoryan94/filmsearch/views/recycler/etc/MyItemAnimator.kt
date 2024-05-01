package com.igoryan94.filmsearch.views.recycler.etc

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class ExampleItemAnimator : DefaultItemAnimator() {
    override fun onAddStarting(item: RecyclerView.ViewHolder?) {
        super.onAddStarting(item)
    }

    override fun onRemoveStarting(item: RecyclerView.ViewHolder?) {
        super.onRemoveStarting(item)
    }

}