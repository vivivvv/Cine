package com.app.cinema.history

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.cinema.App
import com.app.cinema.R
import com.app.cinema.databinding.ActivityHistoryBinding
import com.app.cinema.history.adapter.HistoryAdapter
import com.app.cinema.model.Search
import com.app.cinema.roomdp.HistoryData

class HistoryActivity : AppCompatActivity(), HistoryAdapter.HistoryListClickListener {

    private lateinit var binding: ActivityHistoryBinding
    lateinit var historyListRecycler: RecyclerView
    lateinit var historyListAdapter: HistoryAdapter
    private val viewModel: HistoryViewModel by lazy {
        val factory = HistoryViewModel.HistoryViewModelFactory((application as App).repository)
        ViewModelProvider(this, factory)[HistoryViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        binding.historyViewModel = viewModel
        binding.lifecycleOwner = this@HistoryActivity

        init()
    }

    private fun init() {
        // Initialize recycler view
        historyListRecycler = binding.historyRecyclerview
        // Set historyList recyclerView Data
        setHistoryListRecycler()
        // Getting history
        getHistoryData()
    }

    private fun getHistoryData() {
        viewModel.allHistoryData.observe(this) { data ->
            data?.let {
                Log.d("TAG", "getHistoryData: $data")
                val historyList = ArrayList<HistoryData>()
                data.forEach { historyList.add(it) }
                // Verify history available or not
                if (historyList.isEmpty()) {
                    binding.message.visibility = View.VISIBLE
                    binding.historyRecyclerview.visibility = View.GONE
                } else {
                    binding.message.visibility = View.GONE
                    binding.historyRecyclerview.visibility = View.VISIBLE
                    // Update list data to UI
                    historyListAdapter.setMovieList(historyList)
                }
            }
        }
    }

    private fun setHistoryListRecycler() {
        historyListAdapter = HistoryAdapter()
        historyListRecycler.layoutManager = LinearLayoutManager(this)
        historyListRecycler.adapter = historyListAdapter
        historyListAdapter.setOnClickListener(this)
    }

    override fun onClicked(listPosition: Int, movieList: ArrayList<Search>) {

    }
}