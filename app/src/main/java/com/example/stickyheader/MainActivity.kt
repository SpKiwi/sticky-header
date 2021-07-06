package com.example.stickyheader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.adapter.*
import com.example.stickyheader.adapter.adapter.TestAdapter
import com.example.stickyheader.adapter.adapter.TestViewHolder
import com.example.stickyheader.adapter.model.TestItem
import com.example.stickyheader.adapter.model.generateInitialTestData
import kotlinx.coroutines.flow.flow

class MainActivity : AppCompatActivity() {

    private val itemRecycler: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val stickyItemHeader: TestViewHolder by lazy {
        TestViewHolder(findViewById<View>(R.id.stickyView))
    }
    private val itemAdapter: TestAdapter by lazy {
        TestAdapter(stickyItemHeader) { position ->
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
    }

    private fun updateAdapter(newItems: List<TestItem>, scrollTo: Int) {
        itemAdapter.items = newItems
        itemRecycler.scrollToPosition(scrollTo)
    }

    private var initialTestData: List<TestItem> = generateInitialTestData(0, 20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<View>(R.id.textButton).setOnClickListener {
//            findViewById<TextView>(R.id.textView).text = (++texto).toString()
//        }

        itemAdapter.items = initialTestData
        itemRecycler.apply {
            adapter = itemAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setOnScrollChangeListener(StickyItemScrollListener(itemRecycler, stickyItemHeader) {
                itemAdapter.stickyItemPosition
            })
            stickyItemHeader.itemView.setOnClickListener {
                Log.d("myLog", "Click sticky item")
            }
        }
    }

    fun some() {
        flow<Int> {
            emit(1)
        }.asLiveData()
    }

}