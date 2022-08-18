package com.agvahealthcare.ventilator_ext.api.services

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {

    companion object{

      fun getRetrofit(): Retrofit {
            val gson = GsonBuilder().setLenient().create()
            return Retrofit.Builder()
                .baseUrl("https://logger-server.herokuapp.com")
                .client(createOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }


        private fun createOkHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
            builder.addInterceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url()
                val url = originalUrl.newBuilder()
                    .build()
                val requestBuilder = originalRequest.newBuilder().url(url)
                chain.proceed(requestBuilder.build())
            }
            return builder.build()
        }

    }



}
