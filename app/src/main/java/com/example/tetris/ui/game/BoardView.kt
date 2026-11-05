package com.khalchukov.tetris.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.khalchukov.tetris.R
import com.khalchukov.tetris.game.Board
import kotlin.math.min

class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val density = resources.displayMetrics.density
    private val painter = BlockPainter(context)

    private val fieldPaint = Paint().apply { color = context.getColor(R.color.field) }
    private val gridPaint = Paint().apply {
        color = context.getColor(R.color.grid)
        strokeWidth = density
    }
    private val framePaint = Paint().apply {
        color = context.getColor(R.color.on_surface)
        style = Paint.Style.STROKE
        strokeWidth = 2 * density
    }
    private val flashPaint = Paint().apply {
        color = context.getColor(R.color.flash)
        alpha = 220
    }
    private val ghostPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.secondary_text)
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * density
        pathEffect = DashPathEffect(floatArrayOf(6 * density, 4 * density), 0f)
    }

    private var state: GameUiState? = null
    private var showGrid = true
    private var showGhost = true

    val cellSize: Float
        get() = min(width / Board.WIDTH.toFloat(), height / Board.VISIBLE_HEIGHT.toFloat())

    fun render(state: GameUiState, showGrid: Boolean, showGhost: Boolean) {
        this.state = state
        this.showGrid = showGrid
        this.showGhost = showGhost
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val cs = cellSize
        val fieldW = cs * Board.WIDTH
        val fieldH = cs * Board.VISIBLE_HEIGHT
        val left = (width - fieldW) / 2f
        val top = density

        canvas.drawRect(left, top, left + fieldW, top + fieldH, fieldPaint)

        if (showGrid) {
            for (c in 1 until Board.WIDTH) canvas.drawLine(left + c * cs, top, left + c * cs, top + fieldH, gridPaint)
            for (r in 1 until Board.VISIBLE_HEIGHT) canvas.drawLine(left, top + r * cs, left + fieldW, top + r * cs, gridPaint)
        }

        val s = state
        if (s != null) {
            for (r in Board.HIDDEN_ROWS until Board.HEIGHT) {
                for (c in 0 until Board.WIDTH) {
                    val type = s.board[r][c] ?: continue
                    painter.draw(canvas, left + c * cs, top + (r - Board.HIDDEN_ROWS) * cs, cs, type)
                }
            }

            for (r in s.flashRows) {
                if (r < Board.HIDDEN_ROWS) continue
                val y = top + (r - Board.HIDDEN_ROWS) * cs
                canvas.drawRect(left, y, left + fieldW, y + cs, flashPaint)
            }

            if (showGhost) {
                val inset = 2 * density
                for (cell in s.ghost) {
                    if (cell.y < Board.HIDDEN_ROWS) continue
                    val x = left + cell.x * cs
                    val y = top + (cell.y - Board.HIDDEN_ROWS) * cs
                    canvas.drawRect(x + inset, y + inset, x + cs - inset, y + cs - inset, ghostPaint)
                }
            }

            val type = s.currentType
            if (type != null) {
                for (cell in s.current) {
                    if (cell.y < Board.HIDDEN_ROWS) continue
                    painter.draw(canvas, left + cell.x * cs, top + (cell.y - Board.HIDDEN_ROWS) * cs, cs, type)
                }
            }
        }

        canvas.drawRect(left, top, left + fieldW, top + fieldH, framePaint)
    }
}
