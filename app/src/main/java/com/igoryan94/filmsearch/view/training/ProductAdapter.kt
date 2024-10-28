package com.igoryan94.filmsearch.view.training

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.igoryan94.filmsearch.view.training.recyclerview_models.Item

class ProductAdapter : ListDelegationAdapter<List<Item>>() {

    init {
        delegatesManager.addDelegate(ProductDelegateAdapter())
        delegatesManager.addDelegate(AdDelegateAdapter())
    }

    override fun setItems(items: List<Item>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }
}