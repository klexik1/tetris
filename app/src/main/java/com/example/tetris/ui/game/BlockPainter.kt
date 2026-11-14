package com.khalchukov.tetris.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.khalchukov.tetris.R
import com.khalchukov.tetris.game.PieceType
import kotlin.math.max

class BlockPainter(context: Context) {
    private val colors: Map<PieceType, Int> = mapOf(
        PieceType.I to context.getColor(R.color.piece_i),
        PieceType.O to context.getColor(R.color.piece_o),
        PieceType.T to context.getColor(R.color.piece_t),
        PieceType.S to context.getColor(R.color.piece_s),
        PieceType.Z to context.getColor(R.color.piece_z),
        PieceType.J to context.getColor(R.color.piece_j),
        PieceType.L to context.getColor(R.color.piece_l),
    )
    private val markColor = context.getColor(R.color.block_mark)
    private val borderColor = context.getColor(R.color.field)

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val mark = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = markColor }
    private val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = borderColor
    }
    private val rect = RectF()

    fun draw(canvas: Canvas, left: Float, top: Float, size: Float, type: PieceType) {
        rect.set(left, top, left + size, top + size)
        fill.color = colors.getValue(type)
        canvas.drawRect(rect, fill)

        val step = size * 0.28f
        mark.strokeWidth = size * 0.11f
        when (type) {
            PieceType.I -> Unit
            PieceType.O -> {
                mark.style = Paint.Style.FILL
                val half = size * 0.15f
                canvas.drawRect(
                    left + size / 2 - half, top + size / 2 - half,
                    left + size / 2 + half, top + size / 2 + half, mark
                )
            }
            PieceType.T -> {
                mark.style = Paint.Style.STROKE
                canvas.save()
                canvas.clipRect(rect)
                var o = -size
                while (o < size) {
                    canvas.drawLine(left + o, top + size, left + o + size, top, mark)
                    o += step
                }
                canvas.restore()
            }
            PieceType.S -> {
                mark.style = Paint.Style.STROKE
                var y = top + step / 2
                while (y < top + size) {
                    canvas.drawLine(left, y, left + size, y, mark)
                    y += step
                }
            }
            PieceType.Z -> {
                mark.style = Paint.Style.STROKE
                var x = left + step / 2
                while (x < left + size) {
                    canvas.drawLine(x, top, x, top + size, mark)
                    x += step
                }
            }
            PieceType.J -> {
                mark.style = Paint.Style.FILL
                canvas.drawCircle(left + size / 2, top + size / 2, size * 0.12f, mark)
            }
            PieceType.L -> {
                mark.style = Paint.Style.STROKE
                mark.strokeWidth = max(1f, size * 0.07f)
                val inset = size * 0.22f
                canvas.drawRect(left + inset, top + inset, left + size - inset, top + size - inset, mark)
            }
        }

        border.strokeWidth = max(1f, size * 0.05f)
        val half = border.strokeWidth / 2
        canvas.drawRect(left + half, top + half, left + size - half, top + size - half, border)
    }
}
