package com.igoryan94.filmsearch.utils

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import com.igoryan94.filmsearch.R

@Suppress("DEPRECATION")
//Мы должны унаследоваться от InputMethodService() и применить слушатель KeyboardView.OnKeyboardActionListener и
//переопределить нужные нам методы
class InputMethodServiceImpl : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    //В этом методе мы привязываем все наши верстки созданные ранее и привязываем слушатель
    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.custom_keyboard, null) as KeyboardView
        val keyboard = Keyboard(this, R.xml.num_pad)
        keyboardView.keyboard = keyboard
        keyboardView.setOnKeyboardActionListener(this)

        return keyboardView
    }

    override fun swipeRight() {
    }

    override fun onPress(p0: Int) {
    }

    override fun onRelease(p0: Int) {
    }

    override fun swipeLeft() {
    }

    override fun swipeUp() {
    }

    override fun swipeDown() {
    }

    //Сейчас нам нужно переопределить только этот метод
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        //Вызываем наше соединение с сервисом ввода
        //И обрабатываем нажатия
        currentInputConnection?.commitText(primaryCode.toString(), 1)
    }

    override fun onText(p0: CharSequence?) {
    }
}
