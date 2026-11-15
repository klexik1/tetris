package com.khalchukov.tetris.ui.settings

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.khalchukov.tetris.R
import com.khalchukov.tetris.TetrisApp
import com.khalchukov.tetris.data.prefs.AppLanguage
import com.khalchukov.tetris.data.prefs.AppSettings
import com.khalchukov.tetris.data.prefs.AppTheme
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

        binding.toggleControls.addOnButtonCheckedListener { _, id, checked ->
            if (checked && !syncing) {
                viewModel.setControls(
                    if (id == R.id.btnControlsGestures) ControlScheme.GESTURES else ControlScheme.BUTTONS
                )
            }
        }
        binding.toggleTheme.addOnButtonCheckedListener { _, id, checked ->
            if (checked && !syncing) {
                viewModel.setTheme(if (id == R.id.btnThemeDark) AppTheme.DARK else AppTheme.LIGHT)
            }
        }
        binding.toggleLanguage.addOnButtonCheckedListener { _, id, checked ->
            if (checked && !syncing) {
                viewModel.setLanguage(if (id == R.id.btnLanguageEn) AppLanguage.EN else AppLanguage.RU)
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
        binding.toggleTheme.check(if (s.theme == AppTheme.DARK) R.id.btnThemeDark else R.id.btnThemeLight)
        binding.toggleLanguage.check(if (s.language == AppLanguage.EN) R.id.btnLanguageEn else R.id.btnLanguageRu)
        binding.switchGhost.isChecked = s.ghost
        binding.switchGrid.isChecked = s.grid
        syncing = false
    }
}
