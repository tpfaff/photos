package com.example.tyler.photos.overview.model

import androidx.recyclerview.widget.DiffUtil

sealed class UiState() {
    class Loading : UiState()
    class ListReady(val photos: List<Photo>, val diffResult: DiffUtil.DiffResult, val currentSearch: String?) : UiState()
    class Error : UiState()
}