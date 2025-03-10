package com.igoryan94.filmsearch.view.fragments

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.databinding.FragmentFavoritesBinding
import com.igoryan94.filmsearch.utils.AnimationHelper
import com.igoryan94.filmsearch.view.MainActivity
import com.igoryan94.filmsearch.view.recyclerview_adapters.FilmListRecyclerAdapter
import com.igoryan94.filmsearch.view.recyclerview_adapters.TopSpacingItemDecoration

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavoritesFragment : Fragment() {
    private lateinit var b: FragmentFavoritesBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    init {
        enterTransition = Slide(Gravity.END).apply { duration = 800 }
        returnTransition = Slide(Gravity.END).apply {
            duration = 800
            mode = Slide.MODE_OUT
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentFavoritesBinding.inflate(inflater, container, false)

        val favoritesList: List<Film> = emptyList()

        b.favoritesRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film, position: Int) {
                        (requireActivity() as MainActivity).openFilmDetails(film)
                    }
                })
            //Присваиваем адаптер
            adapter = filmsAdapter
            //Присвои layoutmanager
            layoutManager = LinearLayoutManager(requireContext())
            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
        //Кладем нашу БД в RV
        filmsAdapter.setItems(favoritesList)

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(
            b.favoritesFragmentRoot,
            requireActivity(),
            2
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}