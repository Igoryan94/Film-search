package com.igoryan94.filmsearch.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.activities.MainActivity
import com.igoryan94.filmsearch.databinding.FragmentHomeBinding
import com.igoryan94.filmsearch.views.recycler.adapters.Film
import com.igoryan94.filmsearch.views.recycler.adapters.FilmListRecyclerAdapter
import com.igoryan94.filmsearch.views.recycler.adapters.TopSpacingItemDecoration

class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding

    val filmsDataBase: List<Film> = initFilmsDb()
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    companion object {
        lateinit var instance: HomeFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentHomeBinding.inflate(inflater, container, false)

        instance = this

        initList()
        applyAnimations()

        return b.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("list_position", b.mainRecycler.scrollY)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        b.mainRecycler.scrollToPosition(
            savedInstanceState?.getInt("list_position") ?: 0
        )
    }

    private fun initList() {
        //находим наш RV
        b.mainRecycler.apply {
            //Инициализируем наш адаптер в конструктор передаем анонимно инициализированный интерфейс,
            //оставим его пока пустым, он нам понадобится во второй части задания
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film, position: Int) {
                        (activity as MainActivity).openFilmDetails(film)
                    }
                })
            //Присваиваем адаптер
            adapter = filmsAdapter
            //Присвои layoutmanager
            layoutManager = LinearLayoutManager((activity as MainActivity))
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

        val anim = AnimationUtils.loadAnimation((activity as MainActivity), R.anim.my_animation)
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