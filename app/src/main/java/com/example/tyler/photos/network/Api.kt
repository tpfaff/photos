package com.example.tyler.photos.network

import com.example.tyler.photos.overview.model.PhotosResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Api() {

    val API_KEY = "675894853ae8ec6c242fa4c077bcf4a0"
    val service: ApiService

    init {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor { original ->
                val url = original.request().url().newBuilder().addQueryParameter("api_key", API_KEY).build()
                val authenticatedRequest = original.request().newBuilder().url(url).build()
                original.proceed(authenticatedRequest)
            }
            .addInterceptor(loggingInterceptor)
            .build()


        val retrofit = Retrofit.Builder().baseUrl("https://api.flickr.com/services/")
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build()

        service = retrofit.create(ApiService::class.java)

    }


    fun getPhotos(page: Int, searchTerm: String): Observable<PhotosResponse> {
        return service.getPhotos(page = page, searchTerm = searchTerm)
    }
}