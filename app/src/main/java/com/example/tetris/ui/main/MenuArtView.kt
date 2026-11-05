package com.khalchukov.tetris.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.khalchukov.tetris.R
import kotlin.math.min

class MenuArtView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val blocks: List<Triple<Int, Int, Int>> = buildList {
        fun piece(color: Int, vararg cells: Pair<Int, Int>) = cells.forEach { add(Triple(it.first, it.second, color)) }
        piece(context.getColor(R.color.piece_l), 0 to 4, 1 to 4, 2 to 4, 2 to 3)
        piece(context.getColor(R.color.piece_i), 3 to 4, 4 to 4, 5 to 4, 6 to 4)
        piece(context.getColor(R.color.piece_t), 4 to 3, 5 to 3, 6 to 3, 5 to 2)
        piece(context.getColor(R.color.piece_s), 0 to 3, 1 to 3, 1 to 2, 2 to 2)
        piece(context.getColor(R.color.piece_z), 3 to 0, 4 to 0, 4 to 1, 5 to 1)
    }

    override fun onDraw(canvas: Canvas) {
        val cs = min(width / COLS.toFloat(), height / ROWS.toFloat())
        val left = (width - cs * COLS) / 2f
        val top = (height - cs * ROWS) / 2f
        val gap = cs * 0.04f
        for ((c, r, color) in blocks) {
            paint.color = color
            canvas.drawRect(
                left + c * cs + gap, top + r * cs + gap,
                left + (c + 1) * cs - gap, top + (r + 1) * cs - gap, paint
            )
        }
    }

    private companion object {
        const val COLS = 7
        const val ROWS = 5
    }
}
