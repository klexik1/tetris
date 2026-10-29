package com.khalchukov.tetris.ui.game

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.khalchukov.tetris.R
import com.khalchukov.tetris.TetrisApp
import com.khalchukov.tetris.data.prefs.AppSettings
import com.khalchukov.tetris.data.prefs.ControlScheme
import com.khalchukov.tetris.databinding.ActivityGameBinding
import com.khalchukov.tetris.ui.common.Formatters
import com.khalchukov.tetris.ui.common.factoryOf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val container get() = (application as TetrisApp).container

    private val viewModel: GameViewModel by viewModels {
        factoryOf { GameViewModel(container, intent.getBooleanExtra(EXTRA_RESUME, false)) }
    }

    private val gestures by lazy {
        GestureController(binding.boardView, object : GestureController.Callbacks {
            override fun onLeft() = viewModel.moveLeft()
            override fun onRight() = viewModel.moveRight()
            override fun onRotate() = viewModel.rotate()
            override fun onSoftDrop() = viewModel.softDrop()
            override fun onHardDrop() = viewModel.hardDrop()
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.boardView.setOnTouchListener(gestures)
        binding.btnPause.setOnClickListener { viewModel.pause() }
        binding.btnLeft.setOnTouchListener(RepeatTouchListener { viewModel.moveLeft() })
        binding.btnRight.setOnTouchListener(RepeatTouchListener { viewModel.moveRight() })
        binding.btnSoft.setOnTouchListener(RepeatTouchListener { viewModel.softDrop() })
        binding.btnRotate.setOnClickListener { viewModel.rotate() }
        binding.btnHard.setOnClickListener { viewModel.hardDrop() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (viewModel.state.value.phase) {
                    Phase.PAUSED, Phase.GAME_OVER -> finish()
                    else -> viewModel.pause()
                }
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.state, container.settings.settings) { state, settings -> state to settings }
                    .collect { (state, settings) -> render(state, settings) }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        viewModel.onBackgrounded()
    }

    private fun render(state: GameUiState, settings: AppSettings) {
        val locale = resources.configuration.locales[0]
        binding.boardView.render(state, settings.grid, settings.ghost)
        binding.nextView.setPiece(state.next)
        binding.valueScore.text = Formatters.number(state.score, locale)
        binding.valueLevel.text = state.level.toString()
        binding.valueLines.text = state.lines.toString()

        val buttons = settings.controls == ControlScheme.BUTTONS
        binding.controls.isVisible = buttons
        gestures.enabled = !buttons

        binding.countdownText.isVisible = state.phase == Phase.COUNTDOWN
        binding.countdownText.text = state.countdown.toString()

        when (state.phase) {
            Phase.PAUSED -> showPauseMenu()
            Phase.GAME_OVER -> showGameOver(state, locale)
            else -> binding.overlay.isGone = true
        }
    }

    private fun showPauseMenu() = with(binding) {
        overlay.isVisible = true
        overlayTitle.setText(R.string.pause)
        overlayRecord.isGone = true
        overlayStats.isGone = true
        btnB.isVisible = true
        btnA.setText(R.string.resume)
        btnA.setOnClickListener { viewModel.resumeGame() }
        btnB.setText(R.string.restart)
        btnB.setOnClickListener { viewModel.restart() }
        btnC.setText(R.string.to_menu)
        btnC.setOnClickListener { finish() }
    }

    private fun showGameOver(state: GameUiState, locale: java.util.Locale) = with(binding) {
        overlay.isVisible = true
        overlayTitle.setText(R.string.game_over)
        overlayRecord.isVisible = state.isNewRecord
        overlayStats.isVisible = true
        overlayStats.text = getString(
            R.string.stats_format,
            Formatters.number(state.score, locale),
            state.level,
            state.lines,
            Formatters.duration(state.elapsedSec),
        )
        btnA.setText(R.string.play_again)
        btnA.setOnClickListener { viewModel.restart() }
        btnB.isGone = true
        btnC.setText(R.string.to_menu)
        btnC.setOnClickListener { finish() }
    }

    companion object {
        private const val EXTRA_RESUME = "resume"

        fun newIntent(context: Context, resume: Boolean): Intent =
            Intent(context, GameActivity::class.java).putExtra(EXTRA_RESUME, resume)
    }
}
