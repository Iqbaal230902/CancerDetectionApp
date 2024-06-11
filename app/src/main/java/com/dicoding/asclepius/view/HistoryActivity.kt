package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.view.result.ResultAdapter
import com.dicoding.asclepius.view.result.ResultViewModel
import com.dicoding.asclepius.view.result.ResultViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private var _activityHistoryBinding: ActivityHistoryBinding? = null
    private val binding get() = _activityHistoryBinding

    private lateinit var adapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setSupportActionBar(findViewById(R.id.historyToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        _activityHistoryBinding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.historyToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mainViewModel = obtainViewModel(this@HistoryActivity)
        mainViewModel.getAllHistories().observe(this) { favoriteList ->
            if (favoriteList != null) {
                adapter.setListHistories(favoriteList)
            }
        }

        adapter = ResultAdapter()

        binding?.rvHistory?.layoutManager = LinearLayoutManager(this)
        binding?.rvHistory?.setHasFixedSize(true)
        binding?.rvHistory?.adapter = adapter
    }

    private fun obtainViewModel(activity: AppCompatActivity): ResultViewModel {
        val factory = ResultViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(ResultViewModel::class.java)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}