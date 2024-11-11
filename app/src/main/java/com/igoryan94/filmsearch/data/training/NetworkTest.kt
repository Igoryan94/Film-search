package com.igoryan94.filmsearch.data.training

import com.airbnb.lottie.BuildConfig
import com.google.gson.GsonBuilder
import com.igoryan94.filmsearch.data.training.json_example.UsersData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object NetworkTest {
    fun testNetworkRequest() {
        val gson = GsonBuilder()
            .create()

        // Создаём перехватчик. Если приложение собрано для отладки, включаем базовый логгинг вместо никакого.
        val interceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BASIC
        }
        // Создаем клиент и добавляем туда перехватчик
        val okHttpCLient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        // Создаём объект Retrofit и передаём ему клиент
        val retrofit = Retrofit.Builder()
            .baseUrl("https://reqres.in/")
            // Конвертер. Если у нас нет настроек, можно обойтись без аргумента `gson`
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpCLient)
            .build()
        val service = retrofit.create(RetrofitInterface::class.java)

        service.getUsers(2).enqueue(object : Callback<UsersData> {
            override fun onResponse(call: Call<UsersData>, response: Response<UsersData>) {
                println(response.body())
            }

            override fun onFailure(call: Call<UsersData>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    interface RetrofitInterface {
        @GET("api/users")
        fun getUsers(@Query("page") page: Int): Call<UsersData>
    }
}