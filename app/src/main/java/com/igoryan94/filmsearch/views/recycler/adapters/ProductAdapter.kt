package com.skill_factory.unit6.adapter

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.igoryan94.filmsearch.views.recycler.adapters.AdDelegateAdapter
import com.igoryan94.filmsearch.views.recycler.adapters.ProductDelegateAdapter
import com.igoryan94.filmsearch.views.recycler.models.Item

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