package com.zion830.threedollars.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(
    private val verticalSpacing: Int,
    private val horizontalSpacing: Int = verticalSpacing,
    private val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    constructor(spacing: Int, includeEdge: Boolean = false) : this(spacing, spacing, includeEdge)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager as? GridLayoutManager
        val spanCount = layoutManager?.spanCount ?: 1
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount
            outRect.right = (column + 1) * horizontalSpacing / spanCount
            
            if (position < spanCount) {
                outRect.top = verticalSpacing
            }
            outRect.bottom = verticalSpacing
        } else {
            outRect.left = column * horizontalSpacing / spanCount
            outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount
            
            if (position >= spanCount) {
                outRect.top = verticalSpacing
            }
        }
    }
}