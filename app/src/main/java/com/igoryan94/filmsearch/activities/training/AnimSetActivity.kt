package com.igoryan94.filmsearch.activities.training

import android.animation.Animator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityAnimSetBinding
import com.igoryan94.filmsearch.toast

class AnimSetActivity : AppCompatActivity() {
    lateinit var b: ActivityAnimSetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityAnimSetBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        b.imageSun.animate()
            .setDuration(1500)
            .translationY(-700f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .alpha(1f)
            .start()

        b.imageHorizon.animate()
            .setDuration(2000)
            .alpha(1f)
            .withEndAction {
                b.imageCloud1.animate()
                    .setDuration(1000)
                    .alpha(1f)
                    .translationX(0f)
                    .setStartDelay(750)
                    .start()

                b.imageCloud2.animate()
                    .setDuration(1000)
                    .alpha(1f)
                    .translationX(0f)
                    .setStartDelay(1500)
                    .start()
            }
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    "Наступает утро...".toast(this@AnimSetActivity)
                }

                override fun onAnimationEnd(animation: Animator) {
                    "Кажется, дождик собирается...".toast(this@AnimSetActivity)
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            .start()

        b.imageSun.setOnClickListener {
            b.imageSun.animate()
                .setDuration(300)
                .setInterpolator(AnticipateInterpolator())
                .alpha(0f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }

                })
                .start()
        }
    }
}