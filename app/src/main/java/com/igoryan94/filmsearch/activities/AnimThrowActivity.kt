package com.igoryan94.filmsearch.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

//        throwTheCar()
    }

    private fun mapButtons() {
        b.throwingImage.setOnClickListener {
//            throwTheCar()
            throwTheCarCompletely()
        }
    }

//    private fun throwTheCar() {
//        val throwingAnim = AnimationUtils.loadAnimation(this, R.anim.car_throwing)
//        b.throwingImage.startAnimation(throwingAnim)
//    }

    private fun showAnimDrawable() {
        val ad = ContextCompat.getDrawable(
            this, R.drawable.my_animation_list
        ) as AnimationDrawable

        b.throwingImage.setBackgroundDrawable(ad)
        ad.start()
    }

    private fun throwTheCarCompletely() {
//        val animator = ValueAnimator.ofFloat(1f, 0f)
//
//        animator.addUpdateListener {
//            view.alpha = it.animatedValue as Float
//        }
//        animator.start()

        val animationUpdateListener = object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Toast.makeText(this@AnimThrowActivity, "Animation start", Toast.LENGTH_SHORT).show()
                println("start")
            }

            override fun onAnimationEnd(animation: Animator) {
                Toast.makeText(this@AnimThrowActivity, "Animation End", Toast.LENGTH_SHORT).show()
            }

            override fun onAnimationCancel(animation: Animator) {
                Toast.makeText(this@AnimThrowActivity, "Animation cancel", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onAnimationRepeat(animation: Animator) {
                Toast.makeText(this@AnimThrowActivity, "Animation repeat", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        b.throwingImage.setOnClickListener {
            val anim = ObjectAnimator.ofFloat(b.throwingImage, View.TRANSLATION_Y, 0F, -3000F)
            anim.duration = 3000
            anim.addListener(animationUpdateListener)
            anim.start()

            val anim1 = ObjectAnimator.ofFloat(b.throwingImage, View.TRANSLATION_X, 0F, 700F)
            anim1.duration = 3000
            anim1.addListener(animationUpdateListener)
            anim1.start()

            val anim2 = ObjectAnimator.ofFloat(b.throwingImage, View.ROTATION, 0F, 40F)
            anim2.duration = 3000
            anim2.addListener(animationUpdateListener)
            anim2.start()
        }
    }
}