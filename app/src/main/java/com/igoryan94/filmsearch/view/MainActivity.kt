package com.igoryan94.filmsearch.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.databinding.ActivityMainBinding
import com.igoryan94.filmsearch.utils.toast
import com.igoryan94.filmsearch.view.fragments.FavoritesFragment
import com.igoryan94.filmsearch.view.fragments.FilmDetailsFragment
import com.igoryan94.filmsearch.view.fragments.HomeFragment
import com.igoryan94.filmsearch.view.fragments.SelectionsFragment
import com.igoryan94.filmsearch.view.fragments.SettingsFragment
import com.igoryan94.filmsearch.view.fragments.WatchLaterFragment
import com.igoryan94.filmsearch.view.training.activities.AnimCircularRevealActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private var conditionsChangeReceiver: BroadcastReceiver? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var isManualThemeSet = false

    companion object {
        const val PREFS_NAME = "FilmSearchPrefs"
        const val PREF_MANUAL_THEME = "manual_theme_set"
        const val PREF_THEME_MODE = "theme_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        setupViews()
        setupHomeFragment()
        setupBottomNav()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterConditionsReceiver()
    }

    override fun onResume() {
        super.onResume()
        registerConditionsReceiver()
    }

    private fun initialize() {
        enableEdgeToEdge()
        b = ActivityMainBinding.inflate(layoutInflater)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        loadThemeMode()

        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        unregisterConditionsReceiver()
        conditionsChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_POWER_CONNECTED -> {
                        handlePowerConnected()
                    }

                    Intent.ACTION_BATTERY_LOW -> {
                        handleBatteryLow()
                    }

                    Intent.ACTION_BATTERY_OKAY -> {
                        handleBatteryOkay()
                    }

                    else -> {
                        Timber.w("!!! conditionsChangeReceiver: Got unexpected action: ${intent.action}")
                    }
                }
            }
        }

        registerConditionsReceiver()
    }

    private fun setupViews() {
        setSupportActionBar(b.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_vector_back)

        b.btnTest.setOnClickListener {
            startActivity(Intent(this@MainActivity, AnimCircularRevealActivity::class.java))
        }

        b.topAppBar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount < 2) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else
                onBackPressedDispatcher.onBackPressed()
        }

        b.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> "Настройки".toast(this)
//                R.id.search -> "Поиск".toast(this)
                else -> "-".toast(this)
            }
            true
        }
    }

    // Инициализация центрального фрагмента для навигации по фильмам
    private fun setupHomeFragment() {
        changeFragment(HomeFragment(), "home")
    }

    // Настройка нижней навигации
    private fun setupBottomNav() {
        b.bottomNavigation.selectedItemId = R.id.home

        // Реакция на выбор элементов навигации
        b.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> changeFragment(
                    checkFragmentExistence("home") ?: HomeFragment(),
                    "home"
                )

                R.id.favorites -> changeFragment(
                    checkFragmentExistence("favorites") ?: FavoritesFragment(), "favorites"
                )

                R.id.watchLater -> changeFragment(
                    checkFragmentExistence("watch_later") ?: WatchLaterFragment(), "watch_later"
                )

                R.id.selections -> changeFragment(
                    checkFragmentExistence("selections") ?: SelectionsFragment(), "selections"
                )

                R.id.settings -> changeFragment(
                    checkFragmentExistence("settings") ?: SettingsFragment(), "settings"
                )

                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    // Открытие деталей фильма
    fun openFilmDetails(film: Film) {
        val bundle = Bundle()
        //Первым параметром указывается ключ, по которому потом будем искать, вторым сам
        //передаваемый объект
        bundle.putParcelable("film", film)

        val fragment = FilmDetailsFragment()
        fragment.arguments = bundle

        changeFragment(fragment, "film_details")
    }

    //Ищем фрагмент по тегу, если он есть то возвращаем его, если нет, то null
    private fun checkFragmentExistence(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPlaceholder, fragment, tag)
            .addToBackStack(null)
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_vector_back)
    }

    private fun registerConditionsReceiver() {
        registerReceiver(conditionsChangeReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
        }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) RECEIVER_EXPORTED else 0)
    }

    private fun unregisterConditionsReceiver() {
        if (conditionsChangeReceiver != null) {
            try {
                unregisterReceiver(conditionsChangeReceiver)
            } catch (_: IllegalArgumentException) {
            }
            conditionsChangeReceiver = null
        }
    }


    private fun handlePowerConnected() {
        val text = getString(R.string.condition_receiver_action_power_plugged_toast)
        Timber.d("!!! conditionsChangeReceiver: ACTION_POWER_CONNECTED: $text")
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

        if (!isManualThemeSet) {
            val batteryStatus: Intent? =
                registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = level * 100 / scale.toFloat()

            if (batteryPct > 30) {
                setDayMode()
            } else {
                // На данный момент мы будем считать, что включён дневной режим, если он не установлен вручную
                setDayMode()
            }
        }
    }

    private fun handleBatteryLow() {
        val text = getString(R.string.condition_receiver_action_low_battery_toast)
        Timber.d("!!! conditionsChangeReceiver: ACTION_BATTERY_LOW: $text")
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

        if (!isManualThemeSet) {
            setNightMode()
        }
    }

    private fun handleBatteryOkay() {
        if (!isManualThemeSet) {
            val batteryStatus: Intent? =
                registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging: Boolean =
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

            if (!isCharging) {
                setDayMode()
            }
        }
    }


    private fun setDayMode() {
        applyTheme(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setNightMode() {
        applyTheme(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun applyTheme(mode: Int) {
        if (!isManualThemeSet) {
            if (AppCompatDelegate.getDefaultNightMode() != mode) {
                AppCompatDelegate.setDefaultNightMode(mode)
                saveThemeMode(mode)
                recreate()
            }
        }
    }

    fun setManualThemeMode(mode: Int) {
        isManualThemeSet = true
        AppCompatDelegate.setDefaultNightMode(mode)
        saveThemeMode(mode)
        sharedPreferences.edit().putBoolean(PREF_MANUAL_THEME, true).apply()
        recreate()
    }

    fun resetToAutomaticTheme() {
        isManualThemeSet = false
        sharedPreferences.edit().putBoolean(PREF_MANUAL_THEME, false).apply()
        determineThemeBasedOnBatteryState()
    }

    private fun determineThemeBasedOnBatteryState() {
        val batteryStatus: Intent? =
            registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean =
            status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level * 100 / scale.toFloat()

        if (isCharging || batteryPct > 30) {
            setDayMode()
        } else if (batteryPct <= 15) {
            setNightMode()
        } else { // По умолчанию дневной режим
            setDayMode()
        }
    }

    private fun saveThemeMode(mode: Int) {
        sharedPreferences.edit().putInt(PREF_THEME_MODE, mode).apply()
    }

    private fun loadThemeMode() {
        val savedMode = sharedPreferences.getInt(
            PREF_THEME_MODE,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
        AppCompatDelegate.setDefaultNightMode(savedMode)
        isManualThemeSet =
            sharedPreferences.getBoolean(PREF_MANUAL_THEME, false)
    }
}
