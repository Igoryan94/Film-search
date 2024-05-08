package com.igoryan94.filmsearch.views.recycler.adapters

import android.content.res.Resources
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.igoryan94.filmsearch.R

//в параметр передаем слушатель, чтобы мы потом могли обрабатывать нажатия из класса Activity
class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //В конструктор класс передается layout, который мы создали(film_item.xml)
    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val poster: ImageView = itemView.findViewById(R.id.poster)
        val description: TextView = itemView.findViewById(R.id.description)

        //В этом методе кладем данные из Film в наши View
        fun bind(film: Film) {
            //Устанавливаем заголовок
            title.text = film.title
            //Устанавливаем постер
            poster.setImageResource(film.poster)
            //Устанавливаем описание
            description.text = film.description
        }
    }

    //Здесь у нас хранится список элементов для RV
    private val items = mutableListOf<Film>()

    //Этот метод нужно переопределить на возврат количества элементов в списке RV
    override fun getItemCount() = items.size

    //В этом методе мы привязываем наш ViewHolder и передаем туда "надутую" верстку нашего фильма
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FilmViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        )
    }

    //В этом методе будет привязка полей из объекта Film к View из film_item.xml
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FilmViewHolder -> {
                //Вызываем метод bind(), который мы создали, и передаем туда объект
                //из нашей базы данных с указанием позиции
                holder.bind(items[position])
            }
        }
    }

    //Метод для добавления объектов в наш список
    fun setItems(list: List<Film>) {
        val diffCallback = FilmDiffCallback(items, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        //Сначала очищаем(если не реализовать DiffUtils)
        items.clear()
        //Добавляем
        items.addAll(list)

        //Уведомляем RV, что пришел новый список, и ему нужно заново все "привязывать"
//        notifyDataSetChanged()
        diffResult.dispatchUpdatesTo(this)
    }


    //Интерфейс для обработки кликов
    interface OnItemClickListener {
        fun click(film: Film, position: Int)
    }
}

class TopSpacingItemDecoration(private val paddingInDp: Int) : RecyclerView.ItemDecoration() {
    private val Int.convertPx: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = paddingInDp.convertPx
        outRect.right = paddingInDp.convertPx
        outRect.left = paddingInDp.convertPx

    }
}

class FilmDiffCallback(private val oldList: List<Film>, private val newList: List<Film>) :
    DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

data class Film(
    val title: String,
    val poster: Int,
    val description: String
)