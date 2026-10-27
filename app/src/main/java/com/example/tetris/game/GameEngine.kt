package com.khalchukov.tetris.game

import kotlin.random.Random

class GameEngine(
    random: Random = Random.Default,
    restoreFrom: GameSnapshot? = null,
) {
    private val bag = PieceBag(random)
    private var board = Board()

    var current: Piece? = null
        private set
    var next: PieceType = PieceType.I
        private set
    var score: Int = 0
        private set
    var level: Int = 1
        private set
    var lines: Int = 0
        private set
    var isGameOver: Boolean = false
        private set
    var pendingClearRows: List<Int> = emptyList()
        private set

    val isClearing: Boolean get() = pendingClearRows.isNotEmpty()
    private val isBlocked: Boolean get() = isGameOver || isClearing || current == null

    init {
        if (restoreFrom == null) {
            next = bag.next()
            spawn()
        } else {
            restore(restoreFrom)
        }
    }

    fun moveLeft(): Boolean = tryMove(-1, 0)

    fun moveRight(): Boolean = tryMove(1, 0)

    fun rotate(): Boolean {
        val piece = current ?: return false
        if (isBlocked) return false
        val rotated = piece.rotatedClockwise()
        for (dx in KICKS) {
            val candidate = rotated.moved(dx, 0)
            if (board.fits(candidate)) {
                current = candidate
                return true
            }
        }
        return false
    }

    fun softDrop(): Boolean {
        if (!tryMove(0, 1)) return false
        score += Scoring.SOFT_DROP_POINTS_PER_CELL
        return true
    }

    fun hardDrop() {
        if (isBlocked) return
        var cells = 0
        while (tryMove(0, 1)) cells++
        score += cells * Scoring.HARD_DROP_POINTS_PER_CELL
        lock()
    }

    fun tick() {
        if (isBlocked) return
        if (!tryMove(0, 1)) lock()
    }

    fun finishClear() {
        if (!isClearing) return
        val count = pendingClearRows.size
        score += Scoring.lineClearPoints(count, level)
        board.removeRows(pendingClearRows)
        pendingClearRows = emptyList()
        lines += count
        level = Scoring.levelFor(lines)
        spawn()
    }

    fun boardSnapshot(): List<List<PieceType?>> = board.snapshot()

    fun ghostPiece(): Piece? {
        var ghost = current ?: return null
        while (board.fits(ghost.moved(0, 1))) ghost = ghost.moved(0, 1)
        return ghost
    }

    fun dropIntervalMs(): Long = Scoring.dropIntervalMs(level)

    fun snapshot(elapsedSec: Int): GameSnapshot = GameSnapshot(
        board = board.toIds(),
        current = current?.let { PieceState(it.type.id, it.rotation, it.x, it.y) },
        next = next.id,
        score = score,
        level = level,
        lines = lines,
        elapsedSec = elapsedSec,
    )

    private fun tryMove(dx: Int, dy: Int): Boolean {
        val piece = current ?: return false
        if (isGameOver || isClearing) return false
        val moved = piece.moved(dx, dy)
        if (!board.fits(moved)) return false
        current = moved
        return true
    }

    private fun lock() {
        val piece = current ?: return
        board.place(piece)
        current = null
        val full = board.fullRows()
        if (full.isEmpty()) spawn() else pendingClearRows = full
    }

    private fun spawn() {
        val piece = Piece.spawn(next)
        next = bag.next()
        if (board.fits(piece)) {
            current = piece
        } else {
            current = null
            isGameOver = true
        }
    }

    private fun restore(s: GameSnapshot) {
        board = Board.fromIds(s.board)
        score = s.score
        level = s.level
        lines = s.lines
        next = requireNotNull(PieceType.fromId(s.next)) { "Unknown next piece ${s.next}" }
        val saved = s.current
        if (saved != null) {
            val type = requireNotNull(PieceType.fromId(saved.type)) { "Unknown piece ${saved.type}" }
            val piece = Piece(type, saved.rotation, saved.x, saved.y)
            require(board.fits(piece)) { "Saved piece does not fit the board" }
            current = piece
        } else {
            val full = board.fullRows()
            if (full.isNotEmpty()) {
                pendingClearRows = full
                finishClear()
            } else {
                spawn()
            }
        }
    }

    private companion object {
        val KICKS = intArrayOf(0, -1, 1, -2, 2)
    }
}
