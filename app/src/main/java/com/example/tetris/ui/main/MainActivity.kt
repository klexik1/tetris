package com.khalchukov.tetris.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.khalchukov.tetris.R
import com.khalchukov.tetris.TetrisApp
import com.khalchukov.tetris.databinding.ActivityMainBinding
import com.khalchukov.tetris.ui.common.Formatters
import com.khalchukov.tetris.ui.common.factoryOf
import com.khalchukov.tetris.ui.game.GameActivity
import com.khalchukov.tetris.ui.records.RecordsActivity
import com.khalchukov.tetris.ui.settings.SettingsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var hasSave = false

    private val viewModel: MainViewModel by viewModels {
        factoryOf { MainViewModel((application as TetrisApp).container) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlay.setOnClickListener { onPlay() }
        binding.btnResume.setOnClickListener { startGame(resume = true) }
        binding.btnRecords.setOnClickListener { startActivity(android.content.Intent(this, RecordsActivity::class.java)) }
        binding.btnSettings.setOnClickListener { startActivity(android.content.Intent(this, SettingsActivity::class.java)) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bestScore.collect { best ->
                    binding.textBest.text = getString(R.string.best_score, Formatters.number(best))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            hasSave = viewModel.hasSavedGame()
            binding.btnResume.isVisible = hasSave
        }
    }

    private fun onPlay() {
        if (!hasSave) {
            startGame(resume = false)
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.saved_game_title)
            .setMessage(R.string.saved_game_message)
            .setPositiveButton(R.string.resume) { _, _ -> startGame(resume = true) }
            .setNegativeButton(R.string.restart) { _, _ -> startGame(resume = false) }
            .setNeutralButton(R.string.cancel, null)
            .show()
    }

    private fun startGame(resume: Boolean) {
        startActivity(GameActivity.newIntent(this, resume))
    }
}
