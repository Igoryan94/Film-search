package com.igoryan94.filmsearch.views.recycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.igoryan94.filmsearch.R

class NumbersAdapter(private var numbers: MutableList<Int>) :
    RecyclerView.Adapter<NumberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        return NumberViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.bind(numbers[position])
    }

    override fun getItemCount(): Int {
        return numbers.size
    }

    fun submitList(newNumbers: MutableList<Int>) {
        val diffCallback = NumberDiffCallback(numbers, newNumbers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        numbers = newNumbers
        diffResult.dispatchUpdatesTo(this)
    }
}

class NumberViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_number, parent, false)
) {
    fun bind(number: Int) {
        itemView.findViewById<TextView>(R.id.numberTextView).text = number.toString()
    }
}

class NumberDiffCallback(
    private val oldNumbers: MutableList<Int>,
    private val newNumbers: MutableList<Int>
) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldNumbers.size
    }

    override fun getNewListSize(): Int {
        return newNumbers.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNumbers[oldItemPosition] == newNumbers[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNumbers[oldItemPosition] == newNumbers[newItemPosition]
    }
}