package com.khalchukov.tetris.ui.records

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khalchukov.tetris.R
import com.khalchukov.tetris.data.db.GameResult
import com.khalchukov.tetris.databinding.ItemRecordBinding
import com.khalchukov.tetris.ui.common.Formatters

class RecordsAdapter : ListAdapter<GameResult, RecordsAdapter.Holder>(Diff) {
    class Holder(val binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        val b = holder.binding
        val context = b.root.context
        val top = position < TOP_HIGHLIGHTED

        b.textPlace.text = (position + 1).toString()
        b.textScore.text = Formatters.number(item.score)
        b.textLevel.text = item.level.toString()
        b.textLines.text = item.lines.toString()
        b.textDate.text = Formatters.date(item.playedAt)

        val color = context.getColor(if (top) R.color.on_surface else R.color.secondary_text)
        val style = if (top) Typeface.BOLD else Typeface.NORMAL
        for (tv in listOf(b.textPlace, b.textScore, b.textLevel, b.textLines, b.textDate)) {
            tv.setTextColor(color)
            tv.setTypeface(null, style)
        }
        b.row.setBackgroundColor(if (top) context.getColor(R.color.surface) else 0)
    }

    private object Diff : DiffUtil.ItemCallback<GameResult>() {
        override fun areItemsTheSame(a: GameResult, b: GameResult) = a.id == b.id
        override fun areContentsTheSame(a: GameResult, b: GameResult) = a == b
    }

    private companion object {
        const val TOP_HIGHLIGHTED = 3
    }
}
