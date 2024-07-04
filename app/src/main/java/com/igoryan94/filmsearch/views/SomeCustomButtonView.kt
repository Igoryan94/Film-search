package com.igoryan94.filmsearch.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import timber.log.Timber

class SomeCustomButtonView(context: Context, attrs: AttributeSet) : MaterialButton(context, attrs) {
    private val paint = Paint()

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

    private val dotCoordinatesList = listOf(
        Pair(0, 0),
        Pair(0, height), Pair(width, 0), Pair(width, height)
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Настройка кисти
        paint.apply {
            color = Color.RED
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 5f
        }

        // Заливка чёрным
        canvas.drawColor(Color.BLACK)
        // Рисуем крест диагональными линиями
        canvas.drawLine(0f, 0f, width.toFloat(), height.toFloat(), paint)
        canvas.drawLine(width.toFloat(), 0f, 0f, height.toFloat(), paint)
        // Рисуем точки по углам нашего View
        for (coordPair in dotCoordinatesList)
            canvas.drawPoint(coordPair.first.toFloat(), coordPair.second.toFloat(), paint)
        // Круг
        canvas.drawCircle(width / 2f, height / 2f, 100f, paint.apply { setColor(Color.CYAN) })
        // Квадрат
        canvas.drawRect(
            width / 4f,
            height / 4f,
            width * 3f / 4f,
            height * 3f / 4f,
            paint.apply { setColor(Color.DKGRAY) })

        /**
         * Неправильный (из-за аллокации в onDraw()) метод рисования треугольника:
         * ------
        //Создаем точки при помощи объекта Point
        val a = Point(0, 0)
        val b = Point(0, 100)
        val c = Point(87, 50)

        //Создаем объект Path
        val path = Path()
        //Указываем, как он будет заполняться краской
        path.fillType = FillType.EVEN_ODD
        //Рисуем путь по точкам, созданным ранее
        path.lineTo(b.x.toFloat(), b.y.toFloat())
        path.lineTo(c.x.toFloat(), c.y.toFloat())
        path.lineTo(a.x.toFloat(), a.y.toFloat())
        //"Замыкаем" путь
        path.close()
        //Рисуем на канвасе
        canvas.drawPath(path, paint)
         * ------
         */
    }
}