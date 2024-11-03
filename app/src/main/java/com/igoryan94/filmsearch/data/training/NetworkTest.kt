package com.igoryan94.filmsearch.data.training

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object NetworkTest {
    fun testNetworkRequest() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://reqres.in/api/users/2")
            .build()

        // Создаем отправку запроса, мы должны имплементировать интерфейс Callback,
        // когда будете его импортировать, проверьте, чтобы он был от библиотеки OkHttp, потому что есть
        // Интерфейсы с таким же названием и в других библиотеках
        client.newCall(request).enqueue(object : Callback {
            // Переопределяем метод, что будет, если мы не сможем получить ответ на запрос
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            // Переопределяем метод, что будет, если мы сможем получить ответ на запрос
            override fun onResponse(call: Call, response: Response) {
                // Здесь тоже надо обернуть в try-catch
                try {
                    val responseBody = response.body()
                    println("!!! ${responseBody?.string()}")
                } catch (e: Exception) {
                    println(response)
                    e.printStackTrace()
                }
            }
        })
    }
}