package com.igoryan94.filmsearch.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationBarView
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.activities.training.ImageViewTestActivity
import com.igoryan94.filmsearch.databinding.ActivityMainBinding
import com.igoryan94.filmsearch.toast
import com.igoryan94.filmsearch.views.recycler.adapters.Film
import com.igoryan94.filmsearch.views.recycler.adapters.FilmListRecyclerAdapter
import com.igoryan94.filmsearch.views.recycler.adapters.TopSpacingItemDecoration

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding

    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    val filmsDataBase: List<Film> = initFilmsDb()

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
        initList()
        applyAnimations()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("list_position", b.mainRecycler.scrollY)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)

        b.mainRecycler.scrollToPosition(
            savedInstanceState?.getInt("list_position") ?: 0
        )
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

    private fun initList() {
        //находим наш RV
        b.mainRecycler.apply {
            //Инициализируем наш адаптер в конструктор передаем анонимно инициализированный интерфейс,
            //оставим его пока пустым, он нам понадобится во второй части задания
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film, position: Int) {
                        val bundle = Bundle()
                        //Первым параметром указывается ключ, по которому потом будем искать, вторым сам
                        //передаваемый объект
                        bundle.putParcelable("film", film)
                        startActivity(
                            Intent(
                                this@MainActivity,
                                FilmDetailsActivity::class.java
                            ).putExtras(bundle)
                        )
                    }
                })
            //Присваиваем адаптер
            adapter = filmsAdapter
            //Присвои layoutmanager
            layoutManager = LinearLayoutManager(this@MainActivity)
            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
        //Кладем нашу БД в RV
        filmsAdapter.setItems(filmsDataBase)
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

    private fun initFilmsDb(): List<Film> = listOf(
        Film(
            "Fallout", R.drawable.film_poster_fallout,
            "In a future, post-apocalyptic Los Angeles brought about by nuclear decimation, citizens must live in underground bunkers to protect themselves from radiation, mutants and bandits."
        ),
        Film(
            "The Mandalorian", R.drawable.film_poster_mandalorian,
            "The travels of a lone bounty hunter in the outer reaches of the galaxy, far from the authority of the New Republic."
        ),
        Film(
            "The Walking Dead: Dead City", R.drawable.film_poster_the_walking_dead,
            "Maggie and Negan travel into a post-apocalyptic Manhattan long ago cut off from the mainland. The city is filled with the dead and denizens who have made New York City their own world."
        ),
        Film(
            "Shôgun", R.drawable.film_poster_shogun,
            "When a mysterious European ship is found marooned in a nearby fishing village, Lord Yoshii Toranaga discovers secrets that could tip the scales of power and devastate his enemies."
        ),
        Film(
            "Dune: Part Two", R.drawable.film_poster_dune2,
            "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family."
        ),
        Film(
            "The First Omen", R.drawable.film_poster_omen,
            "A young American woman is sent to Rome to begin a life of service to the church, but encounters a darkness that causes her to question her faith and uncovers a terrifying conspiracy that hopes to bring about the birth of evil incarnate."
        ),
        Film(
            "Jumanji: Welcome to the Jungle", R.drawable.film_poster_jumanji,
            "Four teenagers are sucked into a magical video game, and the only way they can escape is to work together to finish the game."
        ),
        Film(
            "The Lord of the Rings: The Rings of Power", R.drawable.film_poster_lotr_rop,
            "Epic drama set thousands of years before the events of J.R.R. Tolkien's 'The Hobbit' and 'The Lord of the Rings' follows an ensemble cast of characters, both familiar and new, as they confront the long-feared re-emergence of evil to Middle-earth."
        )
    )
}