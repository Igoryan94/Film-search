package com.igoryan94.filmsearch.view.training.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityScrollCoordinatorBinding
import com.igoryan94.filmsearch.utils.toast
import kotlin.math.abs

class ScrollCoordinatorActivity : AppCompatActivity() {
    private lateinit var b: ActivityScrollCoordinatorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityScrollCoordinatorBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        b.toolbar.title = "Film search"

        b.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                b.toolbarLayout.setExpandedTitleColor(
                    ContextCompat.getColor(
                        this@ScrollCoordinatorActivity,
                        R.color.backgroundColor
                    )
                )
                b.toolbarLayout.title = "0"
            } else if (abs(verticalOffset) >= appBarLayout.scrollBarSize) {
                b.toolbarLayout.setCollapsedTitleTextColor(
                    ContextCompat.getColor(
                        this@ScrollCoordinatorActivity,
                        R.color.colorPrimaryLight
                    )
                )
                b.toolbarLayout.title = verticalOffset.toString()
            } else {
                b.toolbarLayout.title = "Film search"
            }
        }

        b.bottomNavigation.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.favorites ->
//                R.id.watchLater->
//                R.id.selections->
//            }
            "${it.title}".toast(this@ScrollCoordinatorActivity)
            true
        }
    }
}