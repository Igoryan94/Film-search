package com.igoryan94.filmsearch.activities.training

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityImageViewTestBinding
import com.igoryan94.filmsearch.log

class ImageViewTestActivity : AppCompatActivity() {
    private lateinit var b: ActivityImageViewTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityImageViewTestBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        b.recyclerView.adapter = MyAdapter()
        PagerSnapHelper().attachToRecyclerView(b.recyclerView)

        val bottomSheetBehavior = BottomSheetBehavior.from(b.bottomSheet)
        b.fab.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                b.fab.alpha = 1 - slideOffset

                b.tintBack.alpha = slideOffset / 2
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                "Bottom sheet state: ${
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> "Hidden"
                        BottomSheetBehavior.STATE_DRAGGING -> "Dragging"
                        BottomSheetBehavior.STATE_COLLAPSED -> "Collapsed"
                        BottomSheetBehavior.STATE_EXPANDED -> "Expanded"
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> "H.-expanded"
                        BottomSheetBehavior.STATE_SETTLING -> "Settling"
                        else -> "Unknown"
                    }
                }".log()
            }
        })
    }

    class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        private val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.YELLOW)

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textView: TextView = itemView.findViewById(R.id.text_view)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_tab, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.itemView.setBackgroundColor(colors[position])
            holder.textView.text = "${position + 1}"
        }

        override fun getItemCount(): Int {
            return colors.size
        }
    }
}