package com.example.stickyheader.adapter

import android.util.Log
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

    private val previousStickyItemAnchor: StickyItemAnchor? = null // todo add previous state to prevent calculations
    private val stickyHeaderView: View get() = stickyViewHolder.itemView

    override fun onScrollChange(view: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val recyclerView = view as RecyclerView
        val stickyItemPosition = stickyItemPosition()

        val stickyItemAnchor = getPositionInfo(recyclerView, stickyItemPosition)
        if (stickyItemAnchor == null) {
            stickyHeaderView.visibility = View.GONE
            return
        }

        fixStickyItemPositioning(stickyItemAnchor, recyclerView, stickyHeaderView)
        /* TODO check if the following is needed */
        val childInContact = getClosestChildInContact(recyclerView, stickyHeaderView, stickyItemAnchor) ?: return
//
//        if (recyclerView.getChildAdapterPosition(childInContact) == stickyItemPosition) {
//            stickyHeaderView.visibility = View.GONE
//            return
//        }
//        /* TODO until here */
//
//        stickyHeaderView.visibility = View.VISIBLE

//        when (stickyItemAnchor) {
//            StickyItemAnchor.BOTTOM,
//            StickyItemAnchor.TOP -> {
//                stickyHeaderView.visibility = View.VISIBLE
//            }
//            StickyItemAnchor.UNKNOWN -> {
//                TODO()
//            }
//        }.exhaustive
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
//        return StickyItemAnchor.UNKNOWN
    }

    private fun fixStickyItemPositioning(itemAnchor: StickyItemAnchor, parent: ViewGroup, view: View) {
        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)
        view.measure(childWidthSpec, childHeightSpec)

        when (itemAnchor) {
            StickyItemAnchor.TOP -> {
                view.layout(
                    0,
                    0,
                    view.measuredWidth,
                    view.measuredHeight
                )
            }
            StickyItemAnchor.BOTTOM -> {
                view.layout(
                    0,
                    parent.measuredHeight - view.measuredHeight,
                    view.measuredWidth,
                    parent.measuredHeight
                )
            }
            StickyItemAnchor.UNKNOWN -> Unit
        }.exhaustive
    }

    private fun getClosestChildInContact(parent: RecyclerView, currentHeader: View, itemAnchor: StickyItemAnchor)/*: View?*/ {
        var childInContact: View? = null
        when (itemAnchor) {
            StickyItemAnchor.BOTTOM -> {
                val contactPoint = currentHeader.top
                parent.children
                    .asIterable()
                    .reversed()
                    .forEach { child ->
                        if (child.top > contactPoint) {
                            childInContact = child
                            if (parent.getChildAdapterPosition(childInContact!!) == (stickyItemPosition())) {
                                Log.d("myLog", "GONE parent.getChildAdapterPosition(childInContact!!) = ${parent.getChildAdapterPosition(childInContact!!)}, stickyItemPosition()=${stickyItemPosition()}")
                                stickyHeaderView.visibility = View.GONE
                            } else {
                                Log.d("myLog", "VISIBLE parent.getChildAdapterPosition(childInContact!!) = ${parent.getChildAdapterPosition(childInContact!!)}, stickyItemPosition()=${stickyItemPosition()}")
                                stickyHeaderView.visibility = View.VISIBLE
                            }
                            return@forEach
                        }
                    }
            }
            StickyItemAnchor.TOP -> {
                val contactPoint = currentHeader.bottom
                parent.children.forEach { child ->
                    if (child.bottom < contactPoint) {
                        childInContact = child
                        if (parent.getChildAdapterPosition(childInContact!!) == (stickyItemPosition())) {
                            stickyHeaderView.visibility = View.GONE
                        } else {
                            stickyHeaderView.visibility = View.VISIBLE
                        }
                        return@forEach
                    }
                }
            }
            StickyItemAnchor.UNKNOWN -> throw RuntimeException()
        }.exhaustive

//        return childInContact
    }

    private enum class StickyItemAnchor {
        BOTTOM, TOP, UNKNOWN // todo check if unknown is needed
    }

}