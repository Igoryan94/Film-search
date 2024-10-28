package com.igoryan94.filmsearch.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.igoryan94.filmsearch.databinding.FragmentHomeBinding
import com.igoryan94.filmsearch.utils.AnimationHelper
import com.igoryan94.filmsearch.view.MainActivity
import com.igoryan94.filmsearch.view.recyclerview_adapters.Film
import com.igoryan94.filmsearch.view.recyclerview_adapters.FilmListRecyclerAdapter
import com.igoryan94.filmsearch.view.recyclerview_adapters.TopSpacingItemDecoration
import com.igoryan94.filmsearch.viewmodel.HomeFragmentViewModel
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding

    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(HomeFragmentViewModel::class.java)
    }

    private var filmsDataBase = listOf<Film>()
        //Используем backing field
        set(value) {
            // Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            // Если пришло другое значение, то кладем его в переменную
            field = value
            // Обновляем RV адаптер
            filmsAdapter.setItems(field)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentHomeBinding.inflate(inflater, container, false)

        instance = this

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(
            b.homeFragmentRoot,
            requireActivity(),
            1
        )

        viewModel.filmsListLiveData.observe(viewLifecycleOwner) {
            filmsDataBase = it
        }

        initList()
        setupSearch()
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

    private fun setupSearch() {
        b.searchView.setOnClickListener {
            b.searchView.isIconified = false
            b.searchView.requestFocusFromTouch()
        }

        //Подключаем слушателя изменений введенного текста в поиска
        b.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            //Этот метод отрабатывает на каждое изменения текста
            override fun onQueryTextChange(newText: String): Boolean {
                //Если ввод пуст то вставляем в адаптер всю БД
                if (newText.isEmpty()) {
                    filmsAdapter.setItems(filmsDataBase)
                    return true
                }

                //Фильтруем список на поискк подходящих сочетаний
                val result = filmsDataBase.filter {
                    //Чтобы все работало правильно, нужно и запрос, и имя фильма приводить к нижнему регистру
                    it.title.lowercase(Locale.getDefault())
                        .contains(newText.lowercase(Locale.getDefault()))
                }

                //Добавляем в адаптер
                filmsAdapter.setItems(result)
                return true
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

    companion object {
        lateinit var instance: HomeFragment
    }
}