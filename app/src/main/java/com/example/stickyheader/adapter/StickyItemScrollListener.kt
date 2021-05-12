package com.example.stickyheader.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.adapter.adapter.TestViewHolder
import com.example.stickyheader.extensions.exhaustive

class StickyItemScrollListener(
    recyclerView: RecyclerView,
    private val stickyViewHolder: TestViewHolder,
    private val stickyItemPosition: () -> Int
) : View.OnScrollChangeListener {

    init {
        recyclerView.post {
            onScrollChange(recyclerView, 0, 0, 0, 0)
        }
    }

    private var previousStickyItemAnchor: StickyItemAnchor? = null
    private val stickyHeaderView: View get() = stickyViewHolder.itemView

    override fun onScrollChange(view: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val recyclerView = view as RecyclerView
        val stickyItemPosition = stickyItemPosition()

        val stickyItemAnchor = getPositionInfo(recyclerView, stickyItemPosition)

        if (stickyHeaderView.height == 0 || previousStickyItemAnchor == stickyItemAnchor) {
            return
        }
        previousStickyItemAnchor = stickyItemAnchor

        if (stickyItemAnchor == null) {
            stickyHeaderView.visibility = View.INVISIBLE
            return
        }

        fixStickyItemPositioning(stickyItemAnchor, recyclerView, stickyHeaderView)
        fixStickyHeaderVisibility(recyclerView, stickyHeaderView, stickyItemAnchor, stickyItemPosition)
    }

    private fun getPositionInfo(parent: RecyclerView, stickyItemPosition: Int): StickyItemAnchor? {
        val topChild = parent.getChildAt(0) ?: return null
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return null
        }
        if (stickyItemPosition <= topChildPosition) {
            return StickyItemAnchor.TOP
        }

        val bottomChild = parent.getChildAt(parent.childCount - 1) ?: return null
        val bottomChildPosition = parent.getChildAdapterPosition(bottomChild)
        if (bottomChildPosition == RecyclerView.NO_POSITION) {
            return null
        }
        if (stickyItemPosition >= bottomChildPosition) {
            return StickyItemAnchor.BOTTOM
        }
        return null
    }

    private fun fixStickyItemPositioning(itemAnchor: StickyItemAnchor, parent: ViewGroup, view: View) {
        when (itemAnchor) {
            StickyItemAnchor.TOP -> {
                view.translationY = 0f
            }
            StickyItemAnchor.BOTTOM -> {
                view.translationY = (parent.height - view.height).toFloat()
            }
        }.exhaustive
    }

    private fun fixStickyHeaderVisibility(parent: RecyclerView, stickyHeader: View, itemAnchor: StickyItemAnchor, stickyItemPosition: Int) {
        val child = parent.children
            .find { child ->
                parent.getChildAdapterPosition(child) == stickyItemPosition
            }

        if (child == null) {
            stickyHeaderView.visibility = View.VISIBLE
            return
        }

        when (itemAnchor) {
            StickyItemAnchor.BOTTOM -> {
                val contactPoint = stickyHeader.top

                if (child.bottom < contactPoint) {
                    stickyHeaderView.visibility = View.INVISIBLE
                } else {
                    stickyHeaderView.visibility = View.VISIBLE
                }
            }
            StickyItemAnchor.TOP -> {
                val contactPoint = stickyHeader.bottom

                if (child.top > contactPoint) {
                    stickyHeaderView.visibility = View.INVISIBLE
                } else {
                    stickyHeaderView.visibility = View.VISIBLE
                }
            }
        }.exhaustive
    }

    private enum class StickyItemAnchor {
        BOTTOM, TOP
    }

}