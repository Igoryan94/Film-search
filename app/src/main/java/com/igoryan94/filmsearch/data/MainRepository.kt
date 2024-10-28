package com.igoryan94.filmsearch.data

import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.view.recyclerview_adapters.Film

class MainRepository {
    val filmsDataBase = listOf(
        Film(
            "Fallout",
            R.drawable.film_poster_fallout,
            "In a future, post-apocalyptic Los Angeles brought about by nuclear decimation, citizens must live in underground bunkers to protect themselves from radiation, mutants and bandits.",
            7.7f
        ),
        Film(
            "The Mandalorian",
            R.drawable.film_poster_mandalorian,
            "The travels of a lone bounty hunter in the outer reaches of the galaxy, far from the authority of the New Republic.",
            7.7f
        ),
        Film(
            "The Walking Dead: Dead City",
            R.drawable.film_poster_the_walking_dead,
            "Maggie and Negan travel into a post-apocalyptic Manhattan long ago cut off from the mainland. The city is filled with the dead and denizens who have made New York City their own world.",
            7.7f
        ),
        Film(
            "Shôgun",
            R.drawable.film_poster_shogun,
            "When a mysterious European ship is found marooned in a nearby fishing village, Lord Yoshii Toranaga discovers secrets that could tip the scales of power and devastate his enemies.",
            7.7f
        ),
        Film(
            "Dune: Part Two",
            R.drawable.film_poster_dune2,
            "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.",
            7.7f
        ),
        Film(
            "The First Omen",
            R.drawable.film_poster_omen,
            "A young American woman is sent to Rome to begin a life of service to the church, but encounters a darkness that causes her to question her faith and uncovers a terrifying conspiracy that hopes to bring about the birth of evil incarnate.",
            7.7f
        ),
        Film(
            "Jumanji: Welcome to the Jungle",
            R.drawable.film_poster_jumanji,
            "Four teenagers are sucked into a magical video game, and the only way they can escape is to work together to finish the game.",
            7.7f
        ),
        Film(
            "The Lord of the Rings: The Rings of Power",
            R.drawable.film_poster_lotr_rop,
            "Epic drama set thousands of years before the events of J.R.R. Tolkien's 'The Hobbit' and 'The Lord of the Rings' follows an ensemble cast of characters, both familiar and new, as they confront the long-feared re-emergence of evil to Middle-earth.",
            7.7f
        )
    )
}