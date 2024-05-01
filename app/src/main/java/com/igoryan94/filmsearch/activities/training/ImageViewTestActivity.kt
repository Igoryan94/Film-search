package com.igoryan94.filmsearch.activities.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityImageViewTestBinding

class ImageViewTestActivity : AppCompatActivity() {
    lateinit var b: ActivityImageViewTestBinding
    private lateinit var pagerAdapter: MyAdapter

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

        //Создаем адаптер
        pagerAdapter = MyAdapter()

        //Привязываем созданный адаптер к нашему ViewPager, который у нас в разметке
        b.viewPager2.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = pagerAdapter

//            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//
//            })

            setPageTransformer { page, position ->
                page.findViewById<TextView>(R.id.textView).translationX =
                    -position * (page.width / 2)
            }
        }

        //Создаем список элементов, который передадим в адаптер
        val pagerItems = listOf(
            PagerItem(ContextCompat.getColor(this, R.color.colorAccent), "Accent"),
            PagerItem(ContextCompat.getColor(this, R.color.colorCat), "Cat"),
            PagerItem(ContextCompat.getColor(this, R.color.backgroundColor), "BG")
        )

        //Передаем список в адаптер
        pagerAdapter.setItems(pagerItems)
    }

    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //В этом методе мы передаем данные из PagerItem в нашу верстку item.xml
        fun onBind(item: PagerItem) {
            //корневой элемент item.xml
            (itemView as? ConstraintLayout)?.apply {
                setBackgroundColor(item.color)
                findViewById<TextView>(R.id.textView).text = item.text
            }
        }
    }

    data class PagerItem(val color: Int, val text: String)

    // Адаптер для ViewPager2
    class MyAdapter : RecyclerView.Adapter<PagerViewHolder>() {
        private var items = mutableListOf<PagerItem>()

        fun setItems(newItems: List<PagerItem>) {
            items.clear()
            items.addAll(newItems)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
            PagerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.tabs_item, parent, false)
            )

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            holder.onBind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }
}