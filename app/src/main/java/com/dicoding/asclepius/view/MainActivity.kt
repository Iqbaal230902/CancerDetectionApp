package com.dicoding.asclepius.view

import ConcreteClassifications
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.google.gson.Gson
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {
    private lateinit var binding: ActivityMainBinding

    private val REQUEST_CODE_PERMISSIONS = 101

    private var currentImageUri: Uri? = null

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCrop = UCrop.of(inputUri, outputUri)
                .withAspectRatio(5F, 5F)
                .withMaxResultSize(224, 224)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return intent?.let { UCrop.getOutput(it) } ?: Uri.EMPTY
        }
    }

    private val cropImage =
        registerForActivityResult(uCropContract) { uri ->
            currentImageUri = uri
            binding.previewImageView.setImageURI(uri)
            if (currentImageUri != null) {
                binding.previewImageView.setImageURI(currentImageUri)
                binding.analyzeButton.visibility = View.VISIBLE
            }
        }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val inputUri = uri
        val outputUri = File(filesDir, "croppedImage.jpg").toUri()

        cropImage.launch(listOf(inputUri, outputUri) as List<Uri>?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbar()

        imageClassifierHelper = ImageClassifierHelper(
            threshold = 0.1f,
            maxResults = 1,
            modelName = "cancer_classification.tflite",
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.let {
                        moveToResult(currentImageUri!!, it)
                    }
                }
            }
        )

        binding.galleryButton.setOnClickListener { openGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
        binding.articleButton.setOnClickListener {
            val intent = Intent(this, ArticleActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpToolbar(){
        binding.homeToolbar.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?) : Boolean {
        return when (item?.itemId){
            R.id.action_history -> {
                val history = Intent(this, HistoryActivity::class.java)
                startActivity(history)
                true
            }
            else -> false

        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchGallery()
        } else {
            requestGalleryPermission()
        }
    }

    private fun requestGalleryPermission() {
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        if (shouldShowRequestPermissionRationale(permission)) {
            Toast.makeText(this, "Gallery access is required to select an image.", Toast.LENGTH_SHORT).show()
        }
        requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSIONS)
    }

    private fun launchGallery() {
        getContent.launch("image/*")
    }

    private fun analyzeImage(uri: Uri) {
        val bitmap = BitmapFactory.decodeFile(uri.toFile().absolutePath)

        imageClassifierHelper.classifyImage(bitmap)
    }

    private fun moveToResult(imageUri: Uri, results: List<Classifications>) {
        val concreteResults = results.map { result ->
            ConcreteClassifications(result.getCategories(), result.getHeadIndex())
        }
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, Gson().toJson(concreteResults))
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}