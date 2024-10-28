package com.igoryan94.filmsearch.view.training

import android.graphics.Canvas
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.igoryan94.filmsearch.R

class MyItemDecoration : RecyclerView.ItemDecoration() {
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        parent.children.forEachIndexed { index, view ->
            (view as TextView).setTextColor(view.context.getColor(R.color.colorText))

            if ((index + 1) % 2 == 0) {
                view.setBackgroundResource(R.color.colorCat)
            } else {
                view.setBackgroundResource(R.color.colorPrimaryDark)
            }
        }
    }
}