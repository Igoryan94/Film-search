package com.igoryan94.filmsearch.view.training.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityAnimCircularRevealBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.math.hypot
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
class AnimCircularRevealActivity : AppCompatActivity() {
    //    lateinit var b: ActivityAnimCircularRevealBinding
    lateinit var b: ActivityAnimCircularRevealBinding

    private var isRevealed = false

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        b = ActivityAnimCircularRevealBinding.inflate(layoutInflater) // TODO вернуть при переиспользовании ViewBinding
        b = DataBindingUtil.inflate(
            layoutInflater, R.layout.activity_anim_circular_reveal,
            null, false
        )
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // DataBinding: задаём текст переменной в макете. Переменная определяет текст у одной из кнопок.
        b.buttonText = ObservableField("DataBinding-defined text")

//        val lottieAnimationView: LottieAnimationView = lottie_anim
//        lottieAnimationView.playAnimation()

        setupFabCircularReveal()
//        setupFabFlingGestures()
//        setupFabSpringGestures()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

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

    private fun setupFabCircularReveal() {
        // Создаем Observable, который будет проверять видимость
        val disposable = Observable.interval(0, 50, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io()) // Операции выполняются в фоновом потоке
            .filter { b.buttonsContainer.isAttachedToWindow } // Проверяем, прикреплена ли view к окну
            .observeOn(AndroidSchedulers.mainThread()) // Возвращаемся в главный поток для изменения UI
            .take(1) // Берем только первый элемент - когда view прикрепится к окну
            .subscribe { setRevealState(!isRevealed) }
        compositeDisposable.add(disposable)

        b.fab.setOnClickListener {
            setRevealState(!isRevealed)
        }
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

//    private var diffX = 0f
//    private var diffY = 0f
//    private fun setupFabSpringGestures() {
//        val springForce = SpringForce(0f).apply {
//            stiffness = SpringForce.STIFFNESS_MEDIUM
//            dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
//        }
//
//        val springAnimationX =
//            SpringAnimation(b.fab, DynamicAnimation.TRANSLATION_X).setSpring(springForce)
//        val springAnimationY =
//            SpringAnimation(b.fab, DynamicAnimation.TRANSLATION_Y).setSpring(springForce)
//
//        b.fab.setOnTouchListener { v, event ->
//            v.performClick()
//            //Проверяем какое действие у нас произошло
//            when (event.action) {
//                //MotionEvent.ACTION_DOWN - вызывается, когда ваш палец коснулся экрана, то есть как бы опустился
//                //вниз, поэтому и DOWN
//                MotionEvent.ACTION_DOWN -> {
//                    //Устанавливаем координаты для корректного перемещения
//                    diffX = event.rawX - v.x
//                    diffY = event.rawY - v.y
//
//                    //Отменяем анимацию, если к примера нашу view еще "пружинит" с предыдущего раза
//                    springAnimationX.cancel()
//                    springAnimationY.cancel()
//                }
//                //MotionEvent.ACTION_MOVE - вызывается, когда мы перемещаем view, то есть меняются координаты
//                //view
//                MotionEvent.ACTION_MOVE -> {
//                    //rawX, rawY текущие координаты view
//                    b.fab.x = event.rawX - diffX
//                    b.fab.y = event.rawY - diffY
//                }
//                //MotionEvent.ACTION_UP - вызывается, когда палец перестал касаться экрана
//                MotionEvent.ACTION_UP -> {
//                    //Стартуем анимацию возвращения в прежнее положение
//                    springAnimationX.start()
//                    springAnimationY.start()
//                }
//            }
//            true
//        }
//    }
}