package com.igoryan94.filmsearch.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationBarView
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.activities.training.ImageViewTestActivity
import com.igoryan94.filmsearch.databinding.ActivityMainBinding
import com.igoryan94.filmsearch.fragments.FilmDetailsFragment
import com.igoryan94.filmsearch.fragments.HomeFragment
import com.igoryan94.filmsearch.toast
import com.igoryan94.filmsearch.views.recycler.adapters.Film

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
        initHomeFragment()
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
                        // TODO доп. точка входа, чтобы тестировать другое активити, если нужно.
                        //  Впоследствии убрать, добавив эту точку входа на отдельную кнопку тестирования
                        startActivity(Intent(this@MainActivity, ImageViewTestActivity::class.java))
                        // -END поменять на что-то полезнее, позже, если нужно

                        return true
                    }

                    else -> return false
                }
            }
        })
    }

    private fun initHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_placeholder, HomeFragment())
            .addToBackStack(null)
            .commit()
    }

    fun openFilmDetails(film: Film) {
        val bundle = Bundle()
        //Первым параметром указывается ключ, по которому потом будем искать, вторым сам
        //передаваемый объект
        bundle.putParcelable("film", film)

        val fragment = FilmDetailsFragment()
        fragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }
}