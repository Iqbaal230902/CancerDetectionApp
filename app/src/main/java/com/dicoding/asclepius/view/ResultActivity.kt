package com.dicoding.asclepius.view

import ConcreteClassifications
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.view.result.ResultAddViewModel
import com.dicoding.asclepius.view.result.ResultViewModelFactory
import com.google.gson.Gson

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var resultAddViewModel: ResultAddViewModel
    private var history: History? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val resultsJson = intent.getStringExtra(EXTRA_RESULT)
        val results = Gson().fromJson(resultsJson, Array<ConcreteClassifications>::class.java)

        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }


            displayResults(results)
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        resultAddViewModel  = obtainViewModel(this@ResultActivity)

        binding.savedResult.setOnClickListener {
            val resultString = StringBuilder()
            for (classification in results) {
                resultString.append(classification.toString())
            }
            history = History(null, resultString.toString(), imageUri.toString())
            resultAddViewModel.insert(history as History)
            Toast.makeText(this, "Successfully saved to History", Toast.LENGTH_SHORT).show()

            binding.savedResult.visibility = View.GONE
        }
    }

    private fun displayResults(results: Array<ConcreteClassifications>) {
        val resultString = StringBuilder()
        for (classification in results) {
            resultString.append(classification.toString())
        }
        binding.resultText.text = resultString.toString()
    }

    private fun obtainViewModel(activity: AppCompatActivity): ResultAddViewModel {
        val factory = ResultViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ResultAddViewModel::class.java]
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}