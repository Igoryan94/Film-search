package com.igoryan94.filmsearch.activities.training

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityListBinding
import com.igoryan94.filmsearch.views.recycler.adapters.MyAdapter
import com.igoryan94.filmsearch.views.recycler.etc.MyItemDecoration

class ListActivity : AppCompatActivity() {
    private lateinit var b: ActivityListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityListBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = MyAdapter()
        b.recyclerView.apply {
            this.adapter = adapter
//            layoutManager = GridLayoutManager(this, 3) // 4; 5
            //Загружаем анимацию, созданную в XML формате
            val anim = AnimationUtils.loadLayoutAnimation(
                applicationContext,
                R.anim.layout_rv_slide_from_right
            )
            //Передаем ее в recyclerView
            layoutAnimation = anim
            //Запускаем анимацию на выполнение
            scheduleLayoutAnimation()

            // Подключаем декоратор элементов
            addItemDecoration(MyItemDecoration())
        }
    }
}