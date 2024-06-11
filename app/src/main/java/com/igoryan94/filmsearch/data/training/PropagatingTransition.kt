package com.igoryan94.filmsearch.data.training

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.transition.CircularPropagation
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionPropagation

class PropagatingTransition(
    private val sceneRoot: ViewGroup,
    private var startingView: View? = null,
    private val transition: Transition,
    duration: Long = 600,
    interpolator: Interpolator = DecelerateInterpolator(),
    propagation: TransitionPropagation = CircularPropagation()
) {
    val targets: Collection<View>

    init {
        targets = (0 until sceneRoot.childCount).map { sceneRoot.getChildAt(it) }
        transition.interpolator = interpolator
        transition.duration = duration
        transition.propagation = propagation
    }

    fun start() {
        if (startingView == null && sceneRoot.childCount > 0) {
            startingView = sceneRoot.getChildAt(0)
        }

        // Устанавливаем реализацию получения эпицентра перехода
        transition.epicenterCallback = (startingView ?: sceneRoot).asEpicenter()

        // Перед началом перехода прячем все view
        targets.forEach { it.visibility = View.INVISIBLE }

        // Завершаем настройки перехода
        TransitionManager.beginDelayedTransition(sceneRoot, transition)

        // Для всех целей анимации меняем видимость
        targets.forEach { it.visibility = View.VISIBLE }
    }

    //Функция для получения реализации эпицентра
    private fun View.asEpicenter(): Transition.EpicenterCallback {
        val viewRect = Rect()
        getGlobalVisibleRect(viewRect)
        return object : Transition.EpicenterCallback() {
            override fun onGetEpicenter(transition: Transition): Rect = viewRect
        }
    }
}
