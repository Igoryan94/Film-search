package com.igoryan94.filmsearch.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationBarView
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.activities.training.ScrollActivity
import com.igoryan94.filmsearch.databinding.ActivityMainBinding
import com.igoryan94.filmsearch.toast

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding

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

        initViews()
        applyAnimations()
    }

    private fun initViews() {
        // TODO доп. точка входа, чтобы тестировать другое активити, если нужно.
        //  Впоследствии убрать, добавив эту точку входа на отдельную кнопку тестирования
        startActivity(Intent(this, ScrollActivity::class.java))
        finish()
        // -END поменять на что-то полезнее, позже, если нужно

        b.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT).show()
        }

        b.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> "Настройки".toast(this)
                else -> "-".toast(this)
            }
            true
        }

        b.bottomNavigation.setOnItemSelectedListener(object :
            NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(it: MenuItem): Boolean {
                when (it.itemId) {
                    R.id.favorites -> {
                        "Избранное".toast(this@MainActivity)
                        return true
                    }

                    R.id.watchLater -> {
                        "Посмотреть позже".toast(this@MainActivity)
                        return true
                    }

                    R.id.selections -> {
                        "Подборки".toast(this@MainActivity)
                        return true
                    }

                    else -> return false
                }
            }
        })
    }

    private fun applyAnimations() {
        b.imagePoster1.animate()
            .translationX(0f)
            .setDuration(1000)
            .start()

        val animator = ObjectAnimator.ofFloat(b.imagePoster2, View.TRANSLATION_Y, 0f)
        animator.setDuration(1000)
        animator.start()

        val anim = AnimationUtils.loadAnimation(this, R.anim.my_animation)
        b.imagePoster3.startAnimation(anim)
    }
}