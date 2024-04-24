package com.igoryan94.filmsearch.activities

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityAnimThrowBinding

class AnimThrowActivity : AppCompatActivity() {
    lateinit var b: ActivityAnimThrowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityAnimThrowBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mapButtons()

        throwTheCar()
    }

    private fun mapButtons() {
        b.throwingImage.setOnClickListener {
            throwTheCar()
        }
    }

    private fun throwTheCar() {
        val throwingAnim = AnimationUtils.loadAnimation(this, R.anim.car_throwing)
        b.throwingImage.startAnimation(throwingAnim)
    }
}