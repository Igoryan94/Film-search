package com.igoryan94.filmsearch.view.recyclerview_adapters

import android.content.res.Resources
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.data.entity.ApiConstants
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.view.custom_views.RatingDonutView

//в параметр передаем слушатель, чтобы мы потом могли обрабатывать нажатия из класса Activity
class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //В конструктор класс передается layout, который мы создали(film_item.xml)
    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val poster: ImageView = itemView.findViewById(R.id.poster)
        val description: TextView = itemView.findViewById(R.id.description)
        private val ratingDonut = itemView.findViewById<RatingDonutView>(R.id.rating)

        //В этом методе кладем данные из Film в наши View
        fun bind(film: Film) {
            //Устанавливаем заголовок
            title.text = film.title
            //Указываем контейнер, в котором будет "жить" наша картинка
            Glide.with(itemView)
                //Загружаем сам ресурс
                .load(ApiConstants.IMAGES_URL + "w342" + film.poster)
                //Центрируем изображение
                .centerCrop()
                //Указываем ImageView, куда будем загружать изображение
                .into(poster)
            //Устанавливаем описание
            description.text = film.description
            //Устанавливаем рейтинг
            ratingDonut.setProgress((film.rating * 10).toInt())
        }
    }

    //Здесь у нас хранится список элементов для RV
    val items = mutableListOf<Film>()

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
                holder.itemView.setOnClickListener {
                    clickListener.click(
                        items[position], position
                    )
                }
            }
        }
    }

    //Метод для переопределения списка
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

    // Метод для добавления объекта в наш список
    fun add(item: Film) {
        val itemsNew = items
        itemsNew.add(item)
        dispatchUpdates(itemsNew)
    }

    // Метод для добавления списка объектов в наш список
    fun add(list: List<Film>) {
        val itemsNew = items
        itemsNew.addAll(list)
        dispatchUpdates(itemsNew)
    }

    // Метод для удаления из списка по индексу
    fun remove(index: Int) {
        val itemsNew = items
        itemsNew.removeAt(index)
        dispatchUpdates(itemsNew)
    }

    // Метод для удаления из списка определённого объекта
    fun remove(film: Film) {
        val itemsNew = items
        itemsNew.remove(film)
        dispatchUpdates(itemsNew)
    }

    private fun dispatchUpdates(itemsNew: List<Film>) {
        val diffCallback = FilmDiffCallback(items, itemsNew)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(itemsNew)

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
