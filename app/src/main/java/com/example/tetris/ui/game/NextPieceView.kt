package com.khalchukov.tetris.ui.game

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.khalchukov.tetris.game.PieceType
import kotlin.math.min

class NextPieceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val painter = BlockPainter(context)
    private var piece: PieceType? = null

    fun setPiece(type: PieceType) {
        if (piece == type) return
        piece = type
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val type = piece ?: return
        val cells = type.cells(0)
        val minX = cells.minOf { it.x }
        val maxX = cells.maxOf { it.x }
        val minY = cells.minOf { it.y }
        val maxY = cells.maxOf { it.y }
        val cols = maxX - minX + 1
        val rows = maxY - minY + 1
        val cs = min(width / 4f, height / 3f)
        val offX = (width - cols * cs) / 2f
        val offY = (height - rows * cs) / 2f
        for (c in cells) painter.draw(canvas, offX + (c.x - minX) * cs, offY + (c.y - minY) * cs, cs, type)
    }
}
