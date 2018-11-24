package com.example.tyler.photos.overview.view


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tyler.photos.R
import com.example.tyler.photos.overview.model.Photo
import com.example.tyler.photos.overview.model.UiState
import com.example.tyler.photos.overview.viewmodel.OverviewFragmentViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.photo_item.view.*


class OverviewFragment : Fragment() {


    private val viewModel: OverviewFragmentViewModel by lazy {
        ViewModelProviders.of(this).get(OverviewFragmentViewModel::class.java)
    }
    private val allSubscriptions = CompositeDisposable()
    val adapter = OverviewAdapter()
    var menu: Menu? = null
    var searchMenuItem: MenuItem? = null

    companion object {
        val TAG = OverviewFragment::class.java.simpleName
        fun newInstance(): OverviewFragment {
            return OverviewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater?.inflate(R.menu.search_menu, menu)
        searchMenuItem = menu?.findItem(R.id.search_view)
        (searchMenuItem?.actionView as SearchView)
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d(TAG, "onQueryTextSubmit $query")
                    viewModel.onSearchTermEntered(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "onQueryTextChange $newText")
                    return true
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = GridLayoutManagerNoPredictiveAnimation(requireContext(), 2)

        //listen for ui state changes from the viewmodel
        allSubscriptions.add(
            viewModel.uiStateChanged
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uiState ->
                    when (uiState) {
                        is UiState.Loading -> showLoadingView()
                        is UiState.ListReady -> {
                            showList(uiState)
                            populateSearchText(uiState)
                        }

                        is UiState.Error -> showErrorView()
                    }
                }, { error ->
                    Log.e(TAG, "Couldn't get UiState", error)
                })
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        allSubscriptions.clear()
    }

    private fun showLoadingView() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun showList(uiState: UiState.ListReady) {
        progress_bar.visibility = View.GONE
        adapter.photos = uiState.photos
        if (recycler_view.adapter == null) {
            recycler_view.adapter = adapter
        }
        uiState.diffResult.dispatchUpdatesTo(adapter)
    }

    // Upon rotation, need to repopulate the search view text
    private fun populateSearchText(uiState: UiState.ListReady) {
        if(uiState.currentSearch.isNullOrEmpty()) return
        searchMenuItem?.let {
            //If this is already expanded, there was no rotation, and there is no need to repopulate it.
            if (!it.isActionViewExpanded) {
                val searchView = it.actionView as SearchView
                    it.expandActionView()
                    //set the text but do not submit the query
                    searchView.setQuery(uiState.currentSearch, false)
            }
        }
    }

    private fun showErrorView() {
        progress_bar.visibility = View.GONE
        Toast.makeText(context, "Couldn't load photos", Toast.LENGTH_LONG).show()
    }

    inner class OverviewAdapter() :
        RecyclerView.Adapter<OverviewAdapter.PhotoViewHolder>() {

        lateinit var photos: List<Photo>

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false))
        }

        override fun getItemCount(): Int {
            return photos.size
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            holder.photo = photos[position]
            if(position == photos.size - 1){
                viewModel.appendPhotos()
            }
        }

        inner class PhotoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            //Using this as a cheap "viewmodel"
            private var _photo: Photo? = null
            var photo: Photo
                get() = _photo!!
                set(value) {
                    _photo = value

                    Glide.with(this@OverviewFragment.requireContext())
                        .load(photo.getUrl())
                        .thumbnail(0.3f)
                        .into(view.photo_imageview)

                    view.setOnClickListener {
                        //todo show full screen
                    }
                }
        }
    }


}
