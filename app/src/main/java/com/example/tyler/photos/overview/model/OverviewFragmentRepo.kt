package com.example.tyler.photos.overview.model

import com.example.tyler.photos.network.Api
import io.reactivex.Observable

class OverviewFragmentRepo {
    val api = Api()
    var currentPhotos: MutableList<Photo> = mutableListOf()
    lateinit var currentFilterList: List<PhotosResponse>
    var oldPhotos: MutableList<Photo> = mutableListOf()
    var currentSearchTerm: String = ""
    var currentPage: Int = 1

    fun getPhotos(page: Int, searchTerm: String): Observable<PhotosResponse> {
        return api.getPhotos(currentPage, currentSearchTerm)
    }
}