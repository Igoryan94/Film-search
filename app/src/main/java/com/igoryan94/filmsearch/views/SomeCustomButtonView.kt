package com.igoryan94.filmsearch.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import timber.log.Timber

class SomeCustomButtonView(context: Context, attrs: AttributeSet) : MaterialButton(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //Считаем полный размер с паддингами
        val heightSize = MeasureSpec.getSize(heightMeasureSpec) + paddingBottom + paddingTop
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) + paddingLeft + paddingRight
        //Получаем конечные размеры View, с учетом режима
        val resolvedWidth = resolveSize(widthSize, widthMeasureSpec)
        val resolvedHeight = resolveSize(heightSize, heightMeasureSpec)
        //Устанавливаем итоговые размеры
        setMeasuredDimension(resolvedWidth, resolvedHeight)

        Timber.d("$widthMeasureSpec x $heightMeasureSpec")
    }
}