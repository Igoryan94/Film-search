package com.igoryan94.filmsearch.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationBarView
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.activities.training.InputTestActivity
import com.igoryan94.filmsearch.databinding.ActivityMainBinding
import com.igoryan94.filmsearch.fragments.FavoritesFragment
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

        setupViews()
        setupHomeFragment()

        setupBottomNav()

//        startActivity(Intent("asdasd")) // кастомный интент для открытия ImageViewTestActivity
    }

    private fun setupViews() {
        setSupportActionBar(b.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_vector_back)

        b.btnTest.setOnClickListener {
            startActivity(Intent(this@MainActivity, InputTestActivity::class.java))
        }

        b.topAppBar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount < 2) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else
                onBackPressedDispatcher.onBackPressed()
        }

        b.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> "Настройки".toast(this)
                else -> "-".toast(this)
            }
            true
        }
    }

    // Инициализация центрального фрагмента для навигации по фильмам
    private fun setupHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentPlaceholder, HomeFragment())
            .addToBackStack(null)
            .commit()
    }

    // Настройка нижней навигации
    private fun setupBottomNav() {
        // Реакция на выбор элементов навигации
        b.bottomNavigation.setOnItemSelectedListener(object :
            NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(it: MenuItem): Boolean {
                when (it.itemId) {
                    R.id.favorites -> {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentPlaceholder, FavoritesFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }

                return false
            }
        })
    }

    // Открытие деталей фильма
    fun openFilmDetails(film: Film) {
        val bundle = Bundle()
        //Первым параметром указывается ключ, по которому потом будем искать, вторым сам
        //передаваемый объект
        bundle.putParcelable("film", film)

        val fragment = FilmDetailsFragment()
        fragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPlaceholder, fragment)
            .addToBackStack(null)
            .commit()
    }
}