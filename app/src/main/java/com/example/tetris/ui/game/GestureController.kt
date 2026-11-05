package com.khalchukov.tetris.ui.game

import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

class GestureController(
    private val board: BoardView,
    private val callbacks: Callbacks,
) : View.OnTouchListener {
    interface Callbacks {
        fun onLeft()
        fun onRight()
        fun onRotate()
        fun onSoftDrop()
        fun onHardDrop()
    }

    var enabled: Boolean = false

    private val slop = ViewConfiguration.get(board.context).scaledTouchSlop
    private var tracker: VelocityTracker? = null
    private var startX = 0f
    private var startY = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var accX = 0f
    private var accY = 0f
    private var downTime = 0L
    private var movedBeyondSlop = false

    override fun onTouch(v: View, e: MotionEvent): Boolean {
        if (!enabled) return false
        val cs = board.cellSize
        if (cs <= 0f) return false
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = e.x; startY = e.y; lastX = e.x; lastY = e.y
                accX = 0f; accY = 0f
                downTime = e.eventTime
                movedBeyondSlop = false
                tracker?.recycle()
                tracker = VelocityTracker.obtain().also { it.addMovement(e) }
            }
            MotionEvent.ACTION_MOVE -> {
                tracker?.addMovement(e)
                accX += e.x - lastX
                accY += e.y - lastY
                lastX = e.x
                lastY = e.y
                if (abs(e.x - startX) > slop || abs(e.y - startY) > slop) movedBeyondSlop = true
                while (accX >= cs) { callbacks.onRight(); accX -= cs }
                while (accX <= -cs) { callbacks.onLeft(); accX += cs }
                if (accY < 0f) accY = 0f
                while (accY >= cs) { callbacks.onSoftDrop(); accY -= cs }
            }
            MotionEvent.ACTION_UP -> {
                tracker?.addMovement(e)
                tracker?.computeCurrentVelocity(1000)
                val vy = tracker?.yVelocity ?: 0f
                val vx = tracker?.xVelocity ?: 0f
                val fastDown = e.y - startY > 2 * cs && vy > HARD_DROP_VELOCITY && abs(vy) > abs(vx)
                if (fastDown) {
                    callbacks.onHardDrop()
                } else if (!movedBeyondSlop && e.eventTime - downTime < TAP_TIMEOUT_MS) {
                    v.performClick()
                    callbacks.onRotate()
                }
                release()
            }
            MotionEvent.ACTION_CANCEL -> release()
        }
        return true
    }

    private fun release() {
        tracker?.recycle()
        tracker = null
    }

    private companion object {
        const val HARD_DROP_VELOCITY = 1800f
        const val TAP_TIMEOUT_MS = 300L
    }
}
