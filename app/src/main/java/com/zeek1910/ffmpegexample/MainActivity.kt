package com.zeek1910.ffmpegexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.zeek1910.ffmpegexample.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val listAdapter = ImageListAdapter()

    private val takeImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { images ->
            Timber.d("images: $images")
            listAdapter.addItems(images)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = listAdapter

        binding.buttonAddImages.setOnClickListener {
            takeImages.launch("image/*")
        }
    }
}