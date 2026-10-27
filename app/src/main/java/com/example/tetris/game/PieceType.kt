package com.khalchukov.tetris.game

enum class PieceType(val id: Int, val size: Int, private val base: List<Cell>) {
    I(1, 4, listOf(Cell(0, 1), Cell(1, 1), Cell(2, 1), Cell(3, 1))),
    O(2, 2, listOf(Cell(0, 0), Cell(1, 0), Cell(0, 1), Cell(1, 1))),
    T(3, 3, listOf(Cell(1, 0), Cell(0, 1), Cell(1, 1), Cell(2, 1))),
    S(4, 3, listOf(Cell(1, 0), Cell(2, 0), Cell(0, 1), Cell(1, 1))),
    Z(5, 3, listOf(Cell(0, 0), Cell(1, 0), Cell(1, 1), Cell(2, 1))),
    J(6, 3, listOf(Cell(0, 0), Cell(0, 1), Cell(1, 1), Cell(2, 1))),
    L(7, 3, listOf(Cell(2, 0), Cell(0, 1), Cell(1, 1), Cell(2, 1)));

    private val rotations: List<List<Cell>> = (0..3).map { r ->
        var cells = base
        repeat(r) { cells = cells.map { Cell(size - 1 - it.y, it.x) } }
        cells
    }

    fun cells(rotation: Int): List<Cell> = rotations[((rotation % 4) + 4) % 4]

    companion object {
        fun fromId(id: Int): PieceType? = values().firstOrNull { it.id == id }
    }
}
