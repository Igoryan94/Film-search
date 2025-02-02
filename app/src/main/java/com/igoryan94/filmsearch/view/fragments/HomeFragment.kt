package com.igoryan94.filmsearch.view.fragments

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.databinding.FragmentHomeBinding
import com.igoryan94.filmsearch.utils.AnimationHelper
import com.igoryan94.filmsearch.view.MainActivity
import com.igoryan94.filmsearch.view.recyclerview_adapters.FilmListRecyclerAdapter
import com.igoryan94.filmsearch.view.recyclerview_adapters.TopSpacingItemDecoration
import com.igoryan94.filmsearch.viewmodel.HomeFragmentViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    private val homeFragmentViewModel: HomeFragmentViewModel by activityViewModels()

    private var filmsDataBase = listOf<Film>()
        // Используем backing field
        set(value) {
            // Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            // Если пришло другое значение, то кладем его в переменную
            field = value
            // Обновляем RV адаптер
            filmsAdapter.setItems(field)
        }

    private val searchSubject = PublishSubject.create<String>()

    private val compositeDisposable = CompositeDisposable()

    private var isLoading = false
    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentHomeBinding.inflate(inflater, container, false)

        val state = homeFragmentViewModel.getState()
        if (state.isNotEmpty()) filmsDataBase = state

        instance = this

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(
            b.homeFragmentRoot, requireActivity(), 1
        )

        initPullToRefresh()
        initList()
        setupSearch()

        // Запрашиваем обновление данных по фильмам
        compositeDisposable.add(homeFragmentViewModel.filmsListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                filmsAdapter.setItems(list)
                Timber.d("!!! home: got data, its size is ${list.size}")
            }
        )

        compositeDisposable.add(homeFragmentViewModel.showProgressBar
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                b.progressBar.isVisible = it
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        homeFragmentViewModel.saveState(filmsDataBase)

        outState.putInt("list_position", b.mainRecycler.scrollY)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        b.mainRecycler.scrollToPosition(
            savedInstanceState?.getInt("list_position") ?: 0
        )
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        searchSubject.onComplete()
        super.onDestroyView()
    }

    private fun setupSearch() {
        b.searchView.setOnClickListener {
            b.searchView.isIconified = false
            b.searchView.requestFocusFromTouch()
        }

        // Подключаем слушателя изменений введенного текста в поиска
        b.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            // Этот метод отрабатывает на каждое изменения текста
            override fun onQueryTextChange(newText: String): Boolean {
                searchSubject.onNext(newText)
                return true
            }
        })

        compositeDisposable.add(
            searchSubject
                // Устанавливаем задержку для более плавного поиска, чтобы не дергался список при наборе каждого символа
                .debounce(300, TimeUnit.MILLISECONDS)
                // Переводим в фоновый поток
                .subscribeOn(Schedulers.io())
                // Обрабатываем в главном потоке, потому что работаем со списком и UI
                .observeOn(AndroidSchedulers.mainThread())
                // Подписываемся на результат
                .subscribe { newText ->
                    val result = filmsDataBase.filter {
                        it.title.lowercase(Locale.getDefault())
                            .contains(newText.lowercase(Locale.getDefault()))
                    }
                    filmsAdapter.setItems(result)
                }
        )
    }

    private fun initList() {
        // находим наш RV
        b.mainRecycler.apply {
            // Инициализируем наш адаптер в конструктор передаем анонимно инициализированный интерфейс,
            // оставим его пока пустым, он нам понадобится во второй части задания
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film, position: Int) {
                        (activity as MainActivity).openFilmDetails(film)
                    }
                })
            // Присваиваем адаптер
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(activity as MainActivity)
            // Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // если не скроллим вниз, выходим из метода
                    if (dy <= 0) return

                    val layoutManager = layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                    // Если не загружаем в данный момент и достигли конца списка
                    if (!isLoading && (visibleItemCount + firstVisibleItem) >= totalItemCount) {
                        // Загружаем следующую страницу
                        isLoading = true
                        currentPage++
                        homeFragmentViewModel.getFilms(currentPage)
                    }
                }
            })
        }

        // Регистрируем слушатель для обновления списка при изменении категории фильмов
        val prefsListener = OnSharedPreferenceChangeListener { _, key ->
            if (key == PreferenceProvider.KEY_DEFAULT_CATEGORY) homeFragmentViewModel.getFilms()
        }
        PreferenceProvider(requireActivity()).getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(prefsListener)
    }

    private fun initPullToRefresh() {
        // Вешаем слушатель, чтобы вызвался pull to refresh
        b.pullToRefresh.setOnRefreshListener {
            // Чистим адаптер(items нужно будет сделать паблик или создать для этого публичный метод)
            filmsAdapter.items.clear()
            // Сбрасываем номер страницы для загрузки первой
            currentPage = 1
            // Сбрасываем флаг загрузки
            isLoading = false
            // Делаем новый запрос фильмов на сервер
            homeFragmentViewModel.getFilms()
            // Убираем крутящееся колечко
            b.pullToRefresh.isRefreshing = false
        }
    }

    companion object {
        lateinit var instance: HomeFragment
    }
}