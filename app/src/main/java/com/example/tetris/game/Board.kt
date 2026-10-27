package com.khalchukov.tetris.game

class Board private constructor(private val grid: Array<Array<PieceType?>>) {
    constructor() : this(emptyGrid())

    operator fun get(x: Int, y: Int): PieceType? = grid[y][x]

    fun isFree(x: Int, y: Int): Boolean =
        x in 0 until WIDTH && y in 0 until HEIGHT && grid[y][x] == null

    fun fits(piece: Piece): Boolean = piece.cells().all { isFree(it.x, it.y) }

    fun place(piece: Piece) {
        for (c in piece.cells()) grid[c.y][c.x] = piece.type
    }

    fun fullRows(): List<Int> =
        (0 until HEIGHT).filter { y -> grid[y].all { it != null } }

    fun removeRows(rows: List<Int>) {
        if (rows.isEmpty()) return
        val kept = (0 until HEIGHT).filter { it !in rows }.map { grid[it] }
        for (i in rows.indices) grid[i] = arrayOfNulls(WIDTH)
        kept.forEachIndexed { i, row -> grid[rows.size + i] = row }
    }

    fun snapshot(): List<List<PieceType?>> = grid.map { it.toList() }

    fun toIds(): List<List<Int>> = grid.map { row -> row.map { it?.id ?: 0 } }

    companion object {
        const val WIDTH = 10
        const val VISIBLE_HEIGHT = 20
        const val HIDDEN_ROWS = 2
        const val HEIGHT = VISIBLE_HEIGHT + HIDDEN_ROWS

        private fun emptyGrid(): Array<Array<PieceType?>> =
            Array(HEIGHT) { arrayOfNulls<PieceType>(WIDTH) }

        fun fromIds(ids: List<List<Int>>): Board {
            require(ids.size == HEIGHT) { "Wrong number of rows: ${ids.size}" }
            val grid = emptyGrid()
            for (y in 0 until HEIGHT) {
                require(ids[y].size == WIDTH) { "Wrong row width in row $y" }
                for (x in 0 until WIDTH) grid[y][x] = PieceType.fromId(ids[y][x])
            }
            return Board(grid)
        }
    }
}
