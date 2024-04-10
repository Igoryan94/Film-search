package com.dragonfly.tweaks

import android.content.Context
import android.util.Log
import android.widget.Toast

/*
 * Базовые типы
 */
// Расширения String
fun String.log(tag: String = "CUR_TEST") = Log.d(tag, this)

fun String.toIntOrDefault(default: Int = -99999) = this.toIntOrNull() ?: default
fun String.toLongOrDefault(default: Long = -99999) = this.toLongOrNull() ?: default
fun String.toFloatOrDefault(default: Float = -99999F) = this.toFloatOrNull() ?: default

fun String.toast(context: Context, long: Boolean = false) =
    Toast.makeText(context, this, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

// Int
fun Int.toast(context: Context, long: Boolean = false) =
    Toast.makeText(context, this, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()


/*
 * View
 */
// ...