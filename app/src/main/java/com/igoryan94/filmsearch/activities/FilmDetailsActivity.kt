package com.igoryan94.filmsearch.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityFilmDetailsBinding
import com.igoryan94.filmsearch.views.recycler.adapters.Film

class FilmDetailsActivity : AppCompatActivity() {
    lateinit var b: ActivityFilmDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityFilmDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupDetails()
    }

    private fun setupDetails() {
        val film = (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            intent.extras?.getParcelable("film")
        else intent.extras?.getParcelable("film", Film::class.java))
            ?: Film(
                "(null)", android.R.drawable.ic_dialog_alert,
                "Object is null"
            )

        b.detailsToolbar.title = film.title
        b.detailsPoster.setImageResource(film.poster)
        b.detailsDescription.text = film.description

        b.detailsFab.setOnClickListener {
            Snackbar.make(
                this@FilmDetailsActivity, b.detailsFab, "Поделились",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}