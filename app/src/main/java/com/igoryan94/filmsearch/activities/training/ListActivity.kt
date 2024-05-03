package com.igoryan94.filmsearch.activities.training

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityListBinding
import com.igoryan94.filmsearch.views.recycler.adapters.MyAdapter
import com.igoryan94.filmsearch.views.recycler.etc.MyItemDecoration

class ListActivity : AppCompatActivity() {
    private lateinit var b: ActivityListBinding

    private lateinit
    var layoutManager: RecyclerView.LayoutManager
    private val KEY_MANAGER_STATE: String = "KeyForLayoutManagerState"

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

        var isLoading = false
        val scrollListener = object : RecyclerView.OnScrollListener() {
            @Override
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as RecyclerView.LayoutManager
                //смотрим сколько элементов на экране
                val visibleItemCount: Int = layoutManager.childCount
                //сколько всего элементов
                val totalItemCount: Int = layoutManager.itemCount

                //какая позиция первого элемента
                val firstVisibleItems =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                //проверяем, грузим мы что-то или нет
                if (!isLoading) {
                    if (visibleItemCount + firstVisibleItems >= totalItemCount) {
                        //ставим флаг, что мы попросили еще элементы
                        isLoading = true
                        //Вызывает загрузку данных в RecyclerView
                    }
                }
            }
        }
        b.recyclerView.addOnScrollListener(scrollListener)

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

        layoutManager = b.recyclerView.layoutManager!!
        @SuppressLint("NewApi")
        fun restoreState() {
            // FIXME устаревший метод getParcelable(String)
            val outState: Parcelable? = savedInstanceState?.getParcelable(KEY_MANAGER_STATE)
            layoutManager.onRestoreInstanceState(outState)
        }
        restoreState()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelable(KEY_MANAGER_STATE, layoutManager.onSaveInstanceState())
    }
}