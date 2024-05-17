package com.igoryan94.filmsearch.activities.training

import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityInputTestBinding

class InputTestActivity : AppCompatActivity() {
    private lateinit var b: ActivityInputTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityInputTestBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val inputFilter = InputFilter { source, start, end, spanned, dstart, dend ->
            // Проверяем длину изменяемого текста
            if (source.length <= 5) {
                // Если длина текста не превышает 5 символов, возвращаем измененный текст
                return@InputFilter source
            }
            // Если длина текста превышает 5 символов, выводим сообщение и оставляем текст не измененным
            Toast.makeText(this, "Character limit is 5", Toast.LENGTH_SHORT).show()
            // Возвращаем оставшийся текст
            spanned.toString()
        }
        // Добавляем фильтр к полю ввода b.username
        b.username.filters = arrayOf(inputFilter)

        val inputFilter1 = InputFilter { source, start, end, spanned, dstart, dend ->
            // Проверяем, содержит ли изменяемый текст символ "e"
            if (source.contains("e")) {
                // Если содержит, выводим сообщение и оставляем текст не измененным
                Toast.makeText(this, "Sorry no e", Toast.LENGTH_SHORT).show()
                // Возвращаем оставшийся текст
                return@InputFilter spanned
            }
            // Если текст не содержит символ "e", возвращаем измененный текст
            source.toString()
        }
        // Добавляем фильтр к полю ввода b.username1
        b.username1.filters = arrayOf(inputFilter1)
    }
}