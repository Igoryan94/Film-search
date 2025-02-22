package com.igoryan94.filmsearch.view.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.igoryan94.filmsearch.FilmNotificationReceiver
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ItemNotificationBinding

class ScheduledNotificationsAdapter(
    private var notifications: MutableList<FilmDetailsFragment.ScheduledNotification>,
    private val fragment: ScheduledNotificationsFragment // Передаем фрагмент для доступа к контексту и функциям
) :
    RecyclerView.Adapter<ScheduledNotificationsAdapter.ViewHolder>() {

    inner class ViewHolder( itemBinding: ItemNotificationBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val filmTitleTextView: TextView = itemBinding.notificationFilmTitle
        val notificationTimeTextView: TextView = itemBinding.notificationTime
        val cancelButton: Button = itemBinding.notificationButtonCancel
        val editButton: Button = itemBinding.notificationButtonEdit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemNotificationBinding.inflate(inflater, parent, false)
        return ViewHolder(itemBinding)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.filmTitleTextView.text = notification.filmTitle
        holder.notificationTimeTextView.text = String.format("%02d:%02d", notification.hour, notification.minute)

        holder.cancelButton.setOnClickListener {
            cancelNotification(position)
        }
        holder.editButton.setOnClickListener {
            editNotification(position)
        }
    }

    override fun getItemCount() = notifications.size

    fun updateData(newNotifications: List<FilmDetailsFragment.ScheduledNotification>) {
        notifications.clear()
        notifications.addAll(newNotifications)
        notifyDataSetChanged()
    }


    private fun cancelNotification(position: Int) {
        val notificationToRemove = notifications[position]

        // 1. Отменить AlarmManager
        val alarmManager = fragment.context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(fragment.context, FilmNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            fragment.context,
            notificationToRemove.requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(pendingIntent)

        // 2. Удалить из SharedPreferences
        removeNotificationFromSharedPref(notificationToRemove.requestCode)

        // 3. Обновить список и RecyclerView
        notifications.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notifications.size)
        fragment.updateNotificationsList() // Обновляем список во фрагменте

        Snackbar.make(
            fragment.requireView(), // Используем requireView() для Fragment
            fragment.getString(R.string.notification_cancelled),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun removeNotificationFromSharedPref(requestCode: Int) {
        val sharedPreferences = fragment.requireContext().getSharedPreferences("watch_later_notifications", Context.MODE_PRIVATE)
        val gson = Gson()
        val notificationsJson = sharedPreferences.getString("notifications", null)
        val notificationsList: MutableList<FilmDetailsFragment.ScheduledNotification> = if (notificationsJson != null) {
            val type = object : TypeToken<MutableList<FilmDetailsFragment.ScheduledNotification>>() {}.type
            gson.fromJson(notificationsJson, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }

        notificationsList.removeAll { it.requestCode == requestCode }

        val editor = sharedPreferences.edit()
        editor.putString("notifications", gson.toJson(notificationsList))
        editor.apply()
    }

    private fun editNotification(position: Int) {
        val notificationToEdit = notifications[position]
        val currentHour = notificationToEdit.hour
        val currentMinute = notificationToEdit.minute

        val timePickerDialog = TimePickerDialog(
            fragment.context,
            { _, selectedHour, selectedMinute ->
                updateNotification(position, selectedHour, selectedMinute)
            },
            currentHour,
            currentMinute,
            true
        )
        timePickerDialog.show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun updateNotification(position: Int, hour: Int, minute: Int) {
        val notificationToUpdate = notifications[position]

        // 1. Отменяем старое уведомление
        val alarmManager = fragment.context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(fragment.context, FilmNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            fragment.context,
            notificationToUpdate.requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(pendingIntent)

        // 2. Планируем новое уведомление
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        val newPendingIntent = PendingIntent.getBroadcast(
            fragment.context,
            notificationToUpdate.requestCode, // Используем тот же requestCode для обновления
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, newPendingIntent)

        // 3. Обновляем данные в SharedPreferences
        updateNotificationInSharedPref(notificationToUpdate.requestCode, hour, minute)

        // 4. Обновляем список и RecyclerView
        notificationToUpdate.hour = hour
        notificationToUpdate.minute = minute
        notifyItemChanged(position)
        fragment.updateNotificationsList() // Обновляем список во фрагменте

        Snackbar.make(
            fragment.requireView(),
            fragment.getString(R.string.notification_updated).format("${hour}:${minute}"),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun updateNotificationInSharedPref(requestCode: Int, hour: Int, minute: Int) {
        val sharedPreferences = fragment.requireContext().getSharedPreferences("watch_later_notifications", Context.MODE_PRIVATE)
        val gson = Gson()
        val notificationsJson = sharedPreferences.getString("notifications", null)
        val notificationsList: MutableList<FilmDetailsFragment.ScheduledNotification> = if (notificationsJson != null) {
            val type = object : TypeToken<MutableList<FilmDetailsFragment.ScheduledNotification>>() {}.type
            gson.fromJson(notificationsJson, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }

        val notificationIndex = notificationsList.indexOfFirst { it.requestCode == requestCode }
        if (notificationIndex != -1) {
            notificationsList[notificationIndex].hour = hour
            notificationsList[notificationIndex].minute = minute
        }

        val editor = sharedPreferences.edit()
        editor.putString("notifications", gson.toJson(notificationsList))
        editor.apply()
    }
}
