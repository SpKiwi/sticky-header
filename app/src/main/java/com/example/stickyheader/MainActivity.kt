package com.example.stickyheader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.adapter.*
import com.example.stickyheader.adapter.model.TestItem
import com.example.stickyheader.adapter.model.generateInitialTestData

class MainActivity : AppCompatActivity() {

    private val itemRecycler: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val itemAdapter: TestAdapter = TestAdapter { position ->
        val scrollToId: Long
        val newTestData = initialTestData
            .toMutableList()
            .apply {
                val previousItem = get(position)
                scrollToId = previousItem.id
                set(position, previousItem.copy(coins = (previousItem.coins + 1)))
            }
            .sortedByDescending { it.coins }
            .mapIndexed { index, testItem ->
                testItem.copy(position = index + 1)
            }

        val scrollPosition = newTestData.indexOfFirst { it.id == scrollToId }
        updateAdapter(newTestData, scrollPosition)
        initialTestData = newTestData
    }

    private fun updateAdapter(newItems: List<TestItem>, scrollTo: Int) {
        itemAdapter.items = newItems
        itemRecycler.scrollToPosition(scrollTo)
    }

    private var initialTestData: List<TestItem> = generateInitialTestData(30, 150)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemRecycler.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
//            addItemDecoration(StickyItemDecoration(this, itemAdapter))
        }
        itemAdapter.items = initialTestData
    }

}