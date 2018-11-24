package com.example.tyler.photos.network

import com.example.tyler.photos.overview.model.PhotosResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("rest/")
    fun getPhotos(
        @Query("method") method: String = "flickr.photos.search",
        @Query("page") page: Int,
        @Query("text") searchTerm: String,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noCallback: Int = 1
    ): Observable<PhotosResponse>
}