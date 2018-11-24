package com.example.tyler.photos.overview.view

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class GridLayoutManagerNoPredictiveAnimation(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount){
    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     * https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position
     */
    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}