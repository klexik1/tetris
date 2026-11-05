package com.khalchukov.tetris.ui.records

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.khalchukov.tetris.R
import com.khalchukov.tetris.TetrisApp
import com.khalchukov.tetris.databinding.ActivityRecordsBinding
import com.khalchukov.tetris.ui.common.factoryOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RecordsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordsBinding
    private val adapter = RecordsAdapter()

    private val viewModel: RecordsViewModel by viewModels {
        factoryOf { RecordsViewModel((application as TetrisApp).container) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter
        binding.btnBack.setOnClickListener { finish() }
        binding.btnClear.setOnClickListener { confirmClear() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.results.collect { items ->
                    adapter.submitList(items)
                    binding.textEmpty.isVisible = items.isEmpty()
                    binding.btnClear.isEnabled = items.isNotEmpty()
                }
            }
        }
    }

    private fun confirmClear() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.clear_title)
            .setMessage(R.string.clear_message)
            .setPositiveButton(R.string.clear) { _, _ -> viewModel.clear() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
