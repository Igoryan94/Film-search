package com.igoryan94.filmsearch.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.igoryan94.filmsearch.activities.MainActivity
import com.igoryan94.filmsearch.databinding.FragmentFilmDetailsBinding
import com.igoryan94.filmsearch.views.recycler.adapters.Film

class FilmDetailsFragment : Fragment() {
    lateinit var b: FragmentFilmDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentFilmDetailsBinding.inflate(inflater, container, false)

        setupDetails()

        return b.root
    }

    @Suppress("DEPRECATION")
    private fun setupDetails() {
        val film = (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable("film")
        else arguments?.getParcelable("film", Film::class.java))
            ?: Film(
                "(null)", android.R.drawable.ic_dialog_alert,
                "Object is null"
            )

        b.detailsToolbar.title = film.title
        b.detailsPoster.setImageResource(film.poster)
        b.detailsDescription.text = film.description

        b.detailsFab.setOnClickListener {
            Snackbar.make(
                activity as MainActivity, b.detailsFab, "Поделились",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}