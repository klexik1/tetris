package com.khalchukov.tetris.game

data class Piece(val type: PieceType, val rotation: Int, val x: Int, val y: Int) {
    fun cells(): List<Cell> = type.cells(rotation).map { Cell(it.x + x, it.y + y) }

    fun moved(dx: Int, dy: Int): Piece = copy(x = x + dx, y = y + dy)

    fun rotatedClockwise(): Piece = copy(rotation = (rotation + 1) % 4)

    companion object {
        fun spawn(type: PieceType): Piece =
            Piece(type, rotation = 0, x = (Board.WIDTH - type.size) / 2, y = 0)
    }
}
