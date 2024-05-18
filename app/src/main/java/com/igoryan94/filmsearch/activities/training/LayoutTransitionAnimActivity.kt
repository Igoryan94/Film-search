package com.igoryan94.filmsearch.activities.training

import android.animation.AnimatorInflater
import android.animation.LayoutTransition
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityLayoutTransitionAnimBinding

class LayoutTransitionAnimActivity : AppCompatActivity() {
    lateinit var b: ActivityLayoutTransitionAnimBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityLayoutTransitionAnimBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        b.buttonAdd.setOnClickListener {
            val imageView = ImageView(this).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@LayoutTransitionAnimActivity,
                        R.drawable.ic_vector_video_library
                    )
                )
                scaleX = 0f
                scaleY = 0f
                alpha = 0f
            }

            b.container.addView(imageView)
        }

        b.buttonRemove.setOnClickListener {
            if (b.container.childCount != 0) {
                b.container.removeViewAt(b.container.childCount - 1)
            }
        }

        b.buttonRemove.setOnLongClickListener {
            b.container.removeAllViews()
            true
        }

        b.container.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        b.container.layoutTransition.setAnimator(
            LayoutTransition.APPEARING,
            AnimatorInflater.loadAnimator(this, R.animator.animator)
        )
    }
}