package com.example.stickyheader.adapter.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.R
import com.example.stickyheader.adapter.adapter.DiffCallback.Companion.PAYLOAD_COINS
import com.example.stickyheader.adapter.adapter.DiffCallback.Companion.PAYLOAD_POSITION
import com.example.stickyheader.adapter.model.TestItem

class TestAdapter(
    private val stickyItem: TestViewHolder,
    private val upClickCallback: (Int) -> Unit
) : RecyclerView.Adapter<TestViewHolder>() {

    var items: List<TestItem> = emptyList()
        set(value) {
            val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(DiffCallback(items, value))
            field = value

            diffResult.dispatchUpdatesTo(this)

            items.find {
                it.isCurrentUser
            }?.let { model ->
                stickyItem.bind(model)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder =
        TestViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle
            bundle.keySet().forEach { key ->
                when (key) {
                    PAYLOAD_POSITION -> holder.bindPosition(items[position])
                    PAYLOAD_COINS -> holder.bindCoins(items[position])
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: TestViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemUpButton.setOnClickListener {
            holder.adapterPosition.let { position ->
                if (position != RecyclerView.NO_POSITION) {
                    upClickCallback(position)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: TestViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemUpButton.setOnClickListener(null)
    }

    val stickyItemPosition: Int get() = items.indexOfFirst { it.isCurrentUser }

}