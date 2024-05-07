package com.igoryan94.filmsearch.activities.training

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityScrollCoordinatorBinding
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

        b.toolbar.title = "Film search\nScrollCoordinatorActivity"

        b.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                b.toolbarLayout.setExpandedTitleColor(
                    ContextCompat.getColor(
                        this@ScrollCoordinatorActivity,
                        R.color.backgroundColor
                    )
                )
            }
            if (abs(verticalOffset) >= appBarLayout.scrollBarSize)
                b.toolbarLayout.setCollapsedTitleTextColor(
                    ContextCompat.getColor(
                        this@ScrollCoordinatorActivity,
                        R.color.colorPrimary
                    )
                )
            b.toolbarLayout.title = verticalOffset.toString()
        }
    }
}