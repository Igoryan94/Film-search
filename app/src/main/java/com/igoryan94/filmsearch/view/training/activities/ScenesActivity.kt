package com.igoryan94.filmsearch.view.training.activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityScenesBinding

class ScenesActivity : AppCompatActivity() {
    private lateinit var b: ActivityScenesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityScenesBinding.inflate(layoutInflater)
        setContentView(b.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(b.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val scene1 = Scene.getSceneForLayout(b.main, R.layout.scene1, this)
        val scene2 = Scene.getSceneForLayout(b.main, R.layout.scene2, this)
        val transitionManager = TransitionManager()

        //Когда зашли на scene1, устанавливаем обработчик нажатия на кнопку, который запустит scene2
        scene1.setEnterAction {
            scene1.sceneRoot.findViewById<Button>(R.id.button)
                .setOnClickListener { transitionManager.transitionTo(scene2) }
        }

        scene2.setEnterAction {
            scene2.sceneRoot.findViewById<Button>(R.id.button)
                .setOnClickListener { transitionManager.transitionTo(scene1) }
        }

        transitionManager.transitionTo(scene1)
    }
}