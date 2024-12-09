package com.igoryan94.filmsearch.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.FragmentSettingsBinding
import com.igoryan94.filmsearch.utils.AnimationHelper
import com.igoryan94.filmsearch.viewmodel.SettingsFragmentViewModel

class SettingsFragment : Fragment() {
    private lateinit var b: FragmentSettingsBinding
    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(SettingsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Подключаем анимации и передаем номер позиции у кнопки в нижнем меню
        AnimationHelper.performFragmentCircularRevealAnimation(
            b.settingsFragmentRoot, requireActivity(), 5
        )
        // Слушаем, какой у нас сейчас выбран вариант в настройках
        viewModel.categoryPropertyLiveData.observe(viewLifecycleOwner) {
            when (it) {
                POPULAR_CATEGORY -> b.radioGroup.check(R.id.radio_popular)
                TOP_RATED_CATEGORY -> b.radioGroup.check(R.id.radio_top_rated)
                UPCOMING_CATEGORY -> b.radioGroup.check(R.id.radio_upcoming)
                NOW_PLAYING_CATEGORY -> b.radioGroup.check(R.id.radio_now_playing)
            }
        }
        // Слушатель для отправки нового состояния в настройки
        b.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_popular -> viewModel.putCategoryProperty(POPULAR_CATEGORY)
                R.id.radio_top_rated -> viewModel.putCategoryProperty(TOP_RATED_CATEGORY)
                R.id.radio_upcoming -> viewModel.putCategoryProperty(UPCOMING_CATEGORY)
                R.id.radio_now_playing -> viewModel.putCategoryProperty(NOW_PLAYING_CATEGORY)
            }
        }
    }

    companion object {
        private const val POPULAR_CATEGORY = "popular"
        private const val TOP_RATED_CATEGORY = "top_rated"
        private const val UPCOMING_CATEGORY = "upcoming"
        private const val NOW_PLAYING_CATEGORY = "now_playing"
    }
}