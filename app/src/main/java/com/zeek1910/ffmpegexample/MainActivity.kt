package com.zeek1910.ffmpegexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.zeek1910.ffmpegexample.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainActivityViewModel>()

    private val listAdapter = ImageListAdapter()

    private val takeImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { images ->
            Timber.d("images: $images")
            viewModel.onImageAdded(images)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.images.onEach { images ->
            listAdapter.addItems(images)
            binding.buttonCreateTimeLaps.isEnabled = images.size > 3
            binding.buttonClear.isEnabled = images.isNotEmpty()
        }.launchIn(lifecycleScope)

        binding.recyclerView.adapter = listAdapter

        binding.buttonAddImages.setOnClickListener { takeImages.launch("image/*") }
        binding.buttonClear.setOnClickListener { viewModel.onButtonClearClicked() }
        binding.buttonCreateTimeLaps.setOnClickListener { }
    }
}