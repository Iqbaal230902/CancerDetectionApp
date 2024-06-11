package com.dicoding.asclepius.view.result

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import com.dicoding.asclepius.helper.HistoryDiffCallback
import com.dicoding.asclepius.view.ResultActivity

class ResultAdapter : RecyclerView.Adapter<ResultAdapter.HistoryViewHolder>() {
    private val listHistories = ArrayList<History>()
    fun setListHistories(listHistories: List<History>) {
        val diffCallback = HistoryDiffCallback(this.listHistories, listHistories)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listHistories.clear()
        this.listHistories.addAll(listHistories)
        diffResult.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listHistories[position])
    }
    override fun getItemCount(): Int {
        return listHistories.size
    }
    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            with(binding) {
                resultPrediction.text = history.result
                val imageUri = Uri.parse(history.image)
                imageUri?.let {
                    binding.historyImage.setImageURI(it)
                }
            }
        }
    }
}