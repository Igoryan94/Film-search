package com.igoryan94.filmsearch.activities.training

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.views.recycler.adapters.NumbersAdapter

class NumberGridActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NumbersAdapter
    private var numbers = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_number_grid)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 7)

        numbers = (1..30).shuffled().toMutableList()

        adapter = NumbersAdapter(numbers)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.button_shuffle).setOnClickListener {
            adapter.submitList(numbers.shuffled().toMutableList())
        }
    }
}