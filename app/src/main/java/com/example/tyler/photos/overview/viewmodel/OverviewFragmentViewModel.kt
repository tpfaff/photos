package com.example.tyler.photos.overview.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.example.tyler.photos.overview.model.OverviewFragmentRepo
import com.example.tyler.photos.overview.model.Photo
import com.example.tyler.photos.overview.model.UiState
import com.example.tyler.photos.overview.view.PhotoListDiffUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject


class OverviewFragmentViewModel : ViewModel() {

    val uiStateChanged = BehaviorSubject.create<UiState>()
    val repo = OverviewFragmentRepo()
    val allSubscriptions = CompositeDisposable()

    companion object {
        val TAG = OverviewFragmentViewModel::class.java.simpleName
    }

    private fun loadPhotos() {
        uiStateChanged.onNext(UiState.Loading())

        allSubscriptions.add(repo.getPhotos(repo.currentPage, repo.currentSearchTerm)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                val newAndOldPhotos = repo.currentPhotos + response.photos.photo
                val diffResult = DiffUtil.calculateDiff(PhotoListDiffUtil(repo.currentPhotos, newAndOldPhotos))
                repo.currentPhotos = newAndOldPhotos.toMutableList()
                uiStateChanged.onNext(UiState.ListReady(repo.currentPhotos as List<Photo>, diffResult, repo.currentSearchTerm))
            }, { error ->
                uiStateChanged.onNext(UiState.Error())
                Log.e(TAG, error.message, error)
            })
        )
    }

    fun onSearchTermEntered(searchTerm: String?) {
        searchTerm?.let {
            repo.currentPage = 0
            repo.currentSearchTerm = searchTerm
            repo.currentPhotos = mutableListOf()
            loadPhotos()
        }
    }

    fun appendPhotos() {
        uiStateChanged.onNext(UiState.Loading())
        repo.currentPage++
        loadPhotos()
    }


    override fun onCleared() {
        super.onCleared()
        allSubscriptions.clear()
    }
}