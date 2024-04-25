package com.igoryan94.filmsearch.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationBarView
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityAnimThrowBinding
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

        // TODO поменять на что-то полезнее, позже, если нужно
        startActivity(Intent(this, AnimSetActivity::class.java))
        b.topBar.setOnClickListener {
            startActivity(Intent(this, AnimSetActivity::class.java))
        }
        b.topBar.setOnLongClickListener {
            startActivity(Intent(this, ActivityAnimThrowBinding::class.java))
            true
        }
        finish()
        // -END поменять на что-то полезнее, позже, если нужно
    }

    private fun applyAnimations() {
        val myAnim = AnimationUtils.loadAnimation(this, R.anim.my_animation)
        b.bottomNavigation.startAnimation(myAnim)
    }
}