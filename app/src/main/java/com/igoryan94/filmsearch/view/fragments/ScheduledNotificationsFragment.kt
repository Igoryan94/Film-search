package com.igoryan94.filmsearch.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.FragmentScheduledNotificationsBinding // Убедитесь, что ViewBinding включен

class ScheduledNotificationsFragment : Fragment() {
    private lateinit var b: FragmentScheduledNotificationsBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScheduledNotificationsAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentScheduledNotificationsBinding.inflate(inflater, container, false)
        recyclerView = b.scheduledNotificationsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ScheduledNotificationsAdapter(mutableListOf(), this) // Передаем фрагмент в адаптер для Context и callback
        recyclerView.adapter = adapter

        sharedPreferences = requireContext().getSharedPreferences("watch_later_notifications", Context.MODE_PRIVATE)

        loadScheduledNotifications()

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.scheduledNotificationsToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadScheduledNotifications() {
        val notificationsJson = sharedPreferences.getString("notifications", null)
        if (notificationsJson != null) {
            val type = object : TypeToken<MutableList<FilmDetailsFragment.ScheduledNotification>>() {}.type // Используем вложенный класс из FilmDetailsFragment
            val notificationsList: MutableList<FilmDetailsFragment.ScheduledNotification> =
                gson.fromJson(notificationsJson, type) ?: mutableListOf()
            adapter.updateData(notificationsList)
        }
    }

    fun updateNotificationsList() { // Функция для обновления списка из адаптера после действий (отмена/редактирование)
        loadScheduledNotifications()
    }
}
