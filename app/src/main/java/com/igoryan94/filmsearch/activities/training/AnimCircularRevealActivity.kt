package com.igoryan94.filmsearch.activities.training

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityAnimCircularRevealBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.hypot
import kotlin.math.roundToInt

class AnimCircularRevealActivity : AppCompatActivity() {
    lateinit var b: ActivityAnimCircularRevealBinding

    private var isRevealed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityAnimCircularRevealBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        CoroutineScope(IO).launch {
            while (true) {
                if (b.buttonsContainer.isAttachedToWindow) {
                    withContext((Main)) {
                        setRevealState(!isRevealed)
                    }
                    break
                }
            }
        }

//        setupFabFlingGestures()

        b.fab.setOnClickListener { setRevealState(!isRevealed) }
    }

//    private fun setupFabFlingGestures() {
//        // Для этой анимации нам нужно два аниматора FlingAnimation, потому что мы анимируем и по X, и по Y.
//        val flingX = FlingAnimation(b.fab, DynamicAnimation.X).apply {
//            friction = 1.1f
//        }
//        val flingY = FlingAnimation(b.fab, DynamicAnimation.Y).apply {
//            friction = 1.1f
//        }
//
//        // Мы будем использовать класс SimpleOnGestureListener, который позволит нам переопределить
//        //только тот метод, который нам нужен, потому как он уже наследует OnGestureListener и переопределил
//        //их за нас, а мы переопределяем только то, что нужно
//        val gesturesListener = object : GestureDetector.SimpleOnGestureListener() {
//            override fun onFling(
//                e1: MotionEvent?,
//                e2: MotionEvent,
//                velocityX: Float,
//                velocityY: Float
//            ): Boolean {
//                flingX.setStartVelocity(velocityX)
//                flingY.setStartVelocity(velocityY)
//
//                flingX.start()
//                flingY.start()
//
//                return true
//            }
//        }
//
//        val gestureDetector = GestureDetector(this, gesturesListener)
//
//        b.fab.setOnTouchListener { v, event ->
//            v.performClick()
//            gestureDetector.onTouchEvent(event)
//        }
//    }

    private fun setRevealState(isRevealing: Boolean) {
        if (isRevealing) {
            // Координаты эпицентра старта круговой анимации,
            //  в данном случае из центра плавающей кнопки
            val x: Int = b.fab.x.roundToInt() + b.fab.width / 2
            val y: Int = b.fab.y.roundToInt() + b.fab.height / 2

            // Рассчитываем радиус на основе длин сторон контейнера
            val endRadius =
                hypot(b.buttonsContainer.width.toDouble(), b.buttonsContainer.height.toDouble())
            // Начальный радиус - нулевой
            val startRadius = 0

            // Создаём анимацию
            val anim = ViewAnimationUtils.createCircularReveal(
                b.buttonsContainer,
                x,
                y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )

            anim.start()
            b.buttonsContainer.visibility = View.VISIBLE
        } else {
            // Обратный процесс

            val x: Int = b.fab.x.roundToInt() + b.fab.width / 2
            val y: Int = b.fab.y.roundToInt() + b.fab.height / 2
            val startRadius: Int = b.mainContainer.width.coerceAtLeast(b.buttonsContainer.height)
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(
                b.buttonsContainer,
                x,
                y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )
            anim.duration = 500

            // Слушатель конца анимации, когда анимация закончится, мы скроем View
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator) {
                    b.buttonsContainer.visibility = View.GONE
                }
            })
            anim.start()
            isRevealed = false
        }

        isRevealed = isRevealing
    }
}