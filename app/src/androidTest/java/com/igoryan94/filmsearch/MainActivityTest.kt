package com.igoryan94.filmsearch

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.igoryan94.filmsearch.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var activityScenario = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun isLoadedChildFragmentWHENStarting() {
        Espresso.onView(ViewMatchers.withId(R.id.fragmentPlaceholder))
            .check(ViewAssertions.matches(ViewMatchers.hasMinimumChildCount(1)))
    }
}