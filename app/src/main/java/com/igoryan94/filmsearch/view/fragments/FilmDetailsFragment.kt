package com.igoryan94.filmsearch.view.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.data.entity.ApiConstants
import com.igoryan94.filmsearch.databinding.FragmentFilmDetailsBinding
import com.igoryan94.filmsearch.view.recyclerview_adapters.Film

class FilmDetailsFragment : Fragment() {
    lateinit var b: FragmentFilmDetailsBinding

    private lateinit var film: Film

    init {
        enterTransition = Slide(Gravity.END).apply { duration = 800 }
        returnTransition = Slide(Gravity.END).apply {
            duration = 800
            mode = Slide.MODE_OUT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentFilmDetailsBinding.inflate(inflater, container, false)

        setupDetails()
        setupFavFab()
        setupShareFab()

        return b.root
    }

    @Suppress("DEPRECATION")
    private fun setupDetails() {
        film = (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable("film")
        else arguments?.getParcelable("film", Film::class.java))
            ?: Film(
                "(null)", "",
                "Object is null"
            )

        b.detailsToolbar.title = film.title
        Glide.with(this)
            .load(ApiConstants.IMAGES_URL + "w780" + film.poster)
            .centerCrop()
            .into(b.detailsPoster)
        b.detailsDescription.text = film.description
    }

    private fun setupFavFab() {
        b.detailsFabFavorites.setImageResource(
            if (film.isInFavorites) R.drawable.ic_vector_fav_filled
            else R.drawable.ic_vector_fav
        )

        b.detailsFabFavorites.setOnClickListener {
            if (!film.isInFavorites) {
                b.detailsFabFavorites.setImageResource(R.drawable.ic_vector_fav)
                film.isInFavorites = true
            } else {
                b.detailsFabFavorites.setImageResource(R.drawable.ic_vector_fav_filled)
                film.isInFavorites = false
            }
        }
    }

    private fun setupShareFab() {
        b.detailsFabShare.setOnClickListener {
            //Создаем интент
            val intent = Intent()
            //Указываем action с которым он запускается
            intent.action = Intent.ACTION_SEND
            //Кладем данные о нашем фильме
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this film: ${film.title}\n\n${film.description}"
            )
            //Указываем MIME тип, чтобы система знала, какое приложения предложить
            intent.type = "text/plain"
            //Запускаем наше активити
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }
}