package com.igoryan94.filmsearch.view.custom_views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.view.training.activities.ImageViewTestActivity

class SubscribeView(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {
    private val editText: EditText
    private val subscribeButton: Button

    init {
        // "Надуваем наш фрагмент"
        LayoutInflater.from(context).inflate(R.layout.subscribe, this)
        //Нужно явно указать ориентацию, если она вертикальная, иначе отображение будет не корректное
        this.orientation = VERTICAL
        // Привязываем наши View из xml (можно и без переменных обращаться к ним сразу напрямую)
        editText = findViewById(R.id.et_subscribe)
        subscribeButton = findViewById(R.id.btn_subscribe)

        //Вешаем слушатель на нашу кнопку, сейчас он отправляет данные из EditText в Toast, в рабочем приложении он бы отправлял данные на сервер
        subscribeButton.setOnClickListener {
            Toast.makeText(context, editText.text, Toast.LENGTH_SHORT).show()

            context.startActivity(Intent(context, ImageViewTestActivity::class.java))
        }
    }
}