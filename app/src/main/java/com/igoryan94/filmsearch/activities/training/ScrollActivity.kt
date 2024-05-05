package com.igoryan94.filmsearch.activities.training

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.views.recycler.adapters.ProductAdapter
import com.igoryan94.filmsearch.views.recycler.models.Ad
import com.igoryan94.filmsearch.views.recycler.models.Product
import java.util.Collections

class ScrollActivity : AppCompatActivity() {
    // TODO: обязательно наверстать тему постраничной прогрузки!!!
    //  Ссылка на урок: https://apps.skillfactory.ru/learning/course/course-v1:SkillFactory+ANDROID-NEW+2020/block-v1:SkillFactory+ANDROID-NEW+2020+type@sequential+block@0e613c01f37d4d4bb1f74f31e623312f/block-v1:SkillFactory+ANDROID-NEW+2020+type@vertical+block@4434b4c23dd24343b7d14939e295f06f
    //  Ссылка на объяснение ментора, почему тема крайне важна: https://app.pachca.com/chats?thread_id=3571584&sidebar_message=234337124
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val adapter = ProductAdapter()
        adapter.items = arrayListOf(
            Product(
                0,
                R.drawable.ic_apple,
                "Apple",
                "Juicy Apple fruit, which is eaten fresh, serves as a raw material in cooking and for making drinks."
            ),
            Ad("Акция", "Скидка на бананы 15%"),
            Product(
                1,
                R.drawable.ic_banana,
                "Banana",
                "It is one of the oldest food crops, and for tropical countries it is the most important food plant and the main export item."
            ),
            Product(
                2,
                R.drawable.ic_lemon,
                "Lemon",
                "Lemons are eaten fresh, and are also used in the manufacture of confectionery and soft drinks, in the liquor and perfume industry."
            ),
            Product(
                3,
                R.drawable.ic_pear,
                "Pear",
                "Under favorable conditions, the pear reaches a large size-up to 5-25 meters in height and 5 meters in diameter of the crown."
            ),
            Product(
                4,
                R.drawable.ic_strawberry,
                "Strawberry",
                "A perennial herbaceous plant 5-20 cm high, with a thick brown rhizome. \"Mustache\" is short. The stem is thin."
            ),
            Product(
                5,
                R.drawable.ic_orange,
                "Orange",
                "Orange juice is widely used as a drink in restaurants and cafes."
            ),
            Product(
                6,
                R.drawable.ic_banana,
                "Banana",
                "It is one of the oldest food crops, and for tropical countries it is the most important food plant and the main export item."
            ),
            Product(
                7,
                R.drawable.ic_banana,
                "Banana",
                "It is one of the oldest food crops, and for tropical countries it is the most important food plant and the main export item."
            ),
            Product(
                8,
                R.drawable.ic_banana,
                "Banana",
                "It is one of the oldest food crops, and for tropical countries it is the most important food plant and the main export item."
            ),
            Product(
                9,
                R.drawable.ic_banana,
                "Banana",
                "It is one of the oldest food crops, and for tropical countries it is the most important food plant and the main export item."
            ),
            Product(
                10,
                R.drawable.ic_banana,
                "Banana",
                "It is one of the oldest food crops, and for tropical countries it is the most important food plant and the main export item."
            ),
            Product(
                11,
                R.drawable.ic_lemon,
                "Lemon",
                "Lemons are eaten fresh, and are also used in the manufacture of confectionery and soft drinks, in the liquor and perfume industry."
            ),
            Product(
                12,
                R.drawable.ic_lemon,
                "Lemon",
                "Lemons are eaten fresh, and are also used in the manufacture of confectionery and soft drinks, in the liquor and perfume industry."
            ),
            Product(
                13,
                R.drawable.ic_lemon,
                "Lemon",
                "Lemons are eaten fresh, and are also used in the manufacture of confectionery and soft drinks, in the liquor and perfume industry."
            ),
        )
        recyclerView.adapter = adapter
        var savePositionFirst = 0
        var savePositionLast = 0

        //Метод который сохраняет текущую позицию скрола
        fun savePosition() {
            savePositionFirst = (recyclerView.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()
            savePositionLast = (recyclerView.layoutManager as LinearLayoutManager)
                .findLastCompletelyVisibleItemPosition()
        }

        //Скролит к savePositionFirst
        fun scrollToSaveStartPosition() {
            recyclerView.smoothScrollToPosition(savePositionFirst)
        }

        //Скролит к savePositionLast
        fun scrollToSaveLastPosition() {
            recyclerView.smoothScrollToPosition(savePositionLast)
        }

        //Метод который скролит список к началу
        // Если уже в начале, прокручивать низ экрана до нижней сохранённой позиции
        fun scrollToStart() {
            if ((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() > 0)
                recyclerView.smoothScrollToPosition(0)
            else
                scrollToSaveLastPosition()
        }

        //Метод который скролит список в конец
        // Если уже в конце, прокручивать верх экрана до верхней сохранённой позиции
        fun scrollToEnd() {
            if ((recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() < adapter.itemCount - 1)
                recyclerView.smoothScrollToPosition(adapter.itemCount)
            else
                scrollToSaveStartPosition()
        }

        val up = findViewById<ImageView>(R.id.up)
        val save = findViewById<ImageView>(R.id.save)
        val down = findViewById<ImageView>(R.id.down)

        up.setOnClickListener {
            scrollToStart()
        }

        save.setOnClickListener {
            savePosition()
        }

        down.setOnClickListener {
            scrollToEnd()
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {


            override fun isLongPressDragEnabled(): Boolean {
                //Drag & drop поддерживается
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                //Swipe поддерживается
                return true
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                //Настраиваем флаги для drag & drop и swipe жестов
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val items = adapter.items
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                //Меняем элементы местами с помощью метода swap
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(items, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(items, i, i - 1)
                    }
                }
                //Сообщаем об изменениях адаптеру
                //Or DiffUtil
                adapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Удаляем элемент из списка после жеста swipe
                (adapter.items as ArrayList).removeAt(viewHolder.adapterPosition)
                //Or DiffUtil
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }
}