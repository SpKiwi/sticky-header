package com.example.stickyheader.adapter.adapter

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.example.stickyheader.adapter.model.TestItem

open class DiffCallback(
    private val oldList: List<TestItem>,
    private val newList: List<TestItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldElement = oldList[oldItemPosition]
        val newElement = newList[newItemPosition]

        val diff = Bundle()

        if (oldElement.position != newElement.position) {
            diff.putByte(PAYLOAD_POSITION, -1)
        }

        if (oldElement.coins != newElement.coins) {
            diff.putByte(PAYLOAD_COINS, -1)
        }

        return if (diff.size() == 0) null else diff
    }

    companion object {
        const val PAYLOAD_POSITION = "PAYLOAD_POSITION"
        const val PAYLOAD_COINS = "PAYLOAD_COINS"
    }
}