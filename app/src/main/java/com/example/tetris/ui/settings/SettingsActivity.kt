package com.khalchukov.tetris.ui.settings

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.khalchukov.tetris.R
import com.khalchukov.tetris.TetrisApp
import com.khalchukov.tetris.data.prefs.AppSettings
import com.khalchukov.tetris.data.prefs.ControlScheme
import com.khalchukov.tetris.databinding.ActivitySettingsBinding
import com.khalchukov.tetris.ui.common.factoryOf
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var syncing = false

    private val viewModel: SettingsViewModel by viewModels {
        factoryOf { SettingsViewModel((application as TetrisApp).container) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.toggleTheme.check(R.id.btnThemeLight)
        binding.toggleLanguage.check(R.id.btnLanguageRu)
        binding.btnThemeDark.setOnTouchListener { _, _ -> true }
        binding.btnLanguageEn.setOnTouchListener { _, _ -> true }

        binding.toggleControls.addOnButtonCheckedListener { _, id, checked ->
            if (checked && !syncing) {
                viewModel.setControls(
                    if (id == R.id.btnControlsGestures) ControlScheme.GESTURES else ControlScheme.BUTTONS
                )
            }
        }
        binding.switchGhost.setOnCheckedChangeListener { _, checked -> if (!syncing) viewModel.setGhost(checked) }
        binding.switchGrid.setOnCheckedChangeListener { _, checked -> if (!syncing) viewModel.setGrid(checked) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings -> if (settings != null) render(settings) }
            }
        }
    }

    private fun render(s: AppSettings) {
        syncing = true
        binding.toggleControls.check(
            if (s.controls == ControlScheme.GESTURES) R.id.btnControlsGestures else R.id.btnControlsButtons
        )
        binding.switchGhost.isChecked = s.ghost
        binding.switchGrid.isChecked = s.grid
        syncing = false
    }
}
