package com.example.stickyheader.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.R

class TestAdapter(
    private val upClickCallback: (Int) -> Unit
) : RecyclerView.Adapter<TestAdapter.TestViewHolder>(), StickyItemDecoration.StickyItemOwner {

    var items: List<TestItem> = emptyList()
        set(value) {
            val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(DiffCallback(items, value))
            field = value

            diffResult.dispatchUpdatesTo(this)
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

    private var stickyItemViewHolder: TestViewHolder? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val stickyItemView = LayoutInflater
            .from(recyclerView.context)
            .inflate(R.layout.recycler_item, recyclerView, false)

//        stickyItemViewHolder = TestViewHolder(stickyItemView).apply { // todo handle it when rebinding data
//            bind(items[stickyItemPosition])
//        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        stickyItemViewHolder = null
    }

    override fun getStickyItemView(): View? =
        stickyItemViewHolder?.itemView

    override fun isStickyItem(itemPosition: Int): Boolean =
        items[itemPosition].isCurrentUser

    override fun getStickyItemPosition(): Int =
        items.indexOfFirst { it.isCurrentUser }

    /* ViewHolders, callbacks, etc. */

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNumber: TextView by lazy { itemView.findViewById<TextView>(R.id.itemNumber) }
        val itemText: TextView by lazy { itemView.findViewById<TextView>(R.id.itemText) }
        val itemUpButton: Button by lazy { itemView.findViewById<Button>(R.id.itemUpButton) }
        val itemCoins: TextView by lazy { itemView.findViewById<TextView>(R.id.itemCoins) }

        fun bind(model: TestItem) {
            itemText.text = model.name
            itemUpButton.visibility = if (model.isCurrentUser) View.GONE else View.VISIBLE

            bindCoins(model)
            bindPosition(model)
        }

        fun bindCoins(model: TestItem) {
            itemCoins.text = model.coins.toString()
        }

        fun bindPosition(model: TestItem) {
            itemNumber.text = model.position.toString()
        }
    }

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
    }

    companion object {
        const val PAYLOAD_POSITION = "PAYLOAD_POSITION"
        const val PAYLOAD_COINS = "PAYLOAD_COINS"
    }

}