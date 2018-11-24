package com.example.tyler.photos.overview.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    val farm: Int,
    val id: String,
    val isfamily: Int,
    val isfriend: Int,
    val ispublic: Int,
    val owner: String,
    val secret: String,
    val server: String,
    val title: String
) : Parcelable{
    fun getUrl() : String{
        return "https://farm${farm}.staticflickr.com/${server}/${id}_${secret}.jpg"
    }
}