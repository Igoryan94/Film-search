package com.igoryan94.filmsearch.activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dragonfly.tweaks.toast
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        b.contents.setOnClickListener {
//            startActivity(Intent(this, ImageViewTestActivity::class.java))
//        }
        setupButtons()
    }

    private fun setupButtons() {
        for (button in listOf(
            b.buttonMenu,
            b.buttonFavorites,
            b.buttonRecommendations,
            b.buttonWatchLater,
            b.buttonSettings
        ))
            button.setOnClickListener { "${(it as Button).text}".toast(this) }
    }
}