package com.khalchukov.tetris.ui.game

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

class RepeatTouchListener(
    private val initialDelayMs: Long = 250,
    private val intervalMs: Long = 70,
    private val action: () -> Unit,
) : View.OnTouchListener {
    private val handler = Handler(Looper.getMainLooper())
    private val repeat = object : Runnable {
        override fun run() {
            action()
            handler.postDelayed(this, intervalMs)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                v.isPressed = true
                action()
                handler.removeCallbacks(repeat)
                handler.postDelayed(repeat, initialDelayMs)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.isPressed = false
                handler.removeCallbacks(repeat)
            }
        }
        return true
    }
}
