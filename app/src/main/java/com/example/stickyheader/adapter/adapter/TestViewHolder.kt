package com.example.stickyheader.adapter.adapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.R
import com.example.stickyheader.adapter.model.TestItem

/* ViewHolders, callbacks, etc. */

class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemNumber: TextView by lazy { itemView.findViewById<TextView>(R.id.itemNumber) }
    val itemText: TextView by lazy { itemView.findViewById<TextView>(R.id.itemText) }
    val itemUpButton: Button by lazy { itemView.findViewById<Button>(R.id.itemUpButton) }
    val itemCoins: TextView by lazy { itemView.findViewById<TextView>(R.id.itemCoins) }

    fun bind(model: TestItem) {
        itemText.text = model.name
        itemUpButton.visibility = if (model.isCurrentUser) View.INVISIBLE else View.VISIBLE

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