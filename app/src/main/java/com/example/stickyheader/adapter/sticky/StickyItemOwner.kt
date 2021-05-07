package com.example.stickyheader.adapter.sticky

import androidx.recyclerview.widget.RecyclerView

interface StickyItemOwner {
    val stickyItemPosition: Int
    val stickyItemViewHolder: RecyclerView.ViewHolder

    /* todo this is redundant, as we already have position */
    fun isStickyItem(position: Int): Boolean
    fun bindStickyItem()
}