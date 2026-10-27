package com.khalchukov.tetris.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {
    private fun emptyBoard(): MutableList<MutableList<Int>> =
        MutableList(Board.HEIGHT) { MutableList(Board.WIDTH) { 0 } }

    private fun snapshot(
        board: List<List<Int>> = emptyBoard(),
        current: PieceState?,
        next: Int = PieceType.O.id,
        score: Int = 0,
        level: Int = 1,
        lines: Int = 0,
    ) = GameSnapshot(board, current, next, score, level, lines, elapsedSec = 0)

    @Test
    fun newGameSpawnsPieceAboveVisibleField() {
        val engine = GameEngine(Random(1))
        val cells = engine.current!!.cells()
        assertTrue(cells.all { it.y < Board.HIDDEN_ROWS })
        assertEquals(1, engine.level)
        assertEquals(0, engine.score)
    }

    @Test
    fun bagGivesEachPieceOnceInSeven() {
        val bag = PieceBag(Random(42))
        val first = List(7) { bag.next() }
        assertEquals(PieceType.values().toSet(), first.toSet())
        val second = List(7) { bag.next() }
        assertEquals(PieceType.values().toSet(), second.toSet())
    }

    @Test
    fun everyPieceHasFourCellsInEveryRotation() {
        for (type in PieceType.values()) for (r in 0..3) {
            val cells = type.cells(r)
            assertEquals(4, cells.toSet().size)
            assertTrue(cells.all { it.x in 0 until type.size && it.y in 0 until type.size })
        }
    }

    @Test
    fun movesStopAtWalls() {
        val engine = GameEngine(Random(3))
        repeat(20) { engine.moveLeft() }
        assertTrue(engine.current!!.cells().minOf { it.x } == 0)
        assertFalse(engine.moveLeft())
        repeat(20) { engine.moveRight() }
        assertTrue(engine.current!!.cells().maxOf { it.x } == Board.WIDTH - 1)
    }

    @Test
    fun hardDropWithLineClearScoresDropAndLine() {
        val board = emptyBoard()
        for (x in 0 until Board.WIDTH - 1) board[Board.HEIGHT - 1][x] = PieceType.T.id
        val engine = GameEngine(
            restoreFrom = snapshot(board, PieceState(PieceType.I.id, rotation = 1, x = 7, y = 0))
        )
        engine.hardDrop()
        assertTrue(engine.isClearing)
        assertEquals(listOf(Board.HEIGHT - 1), engine.pendingClearRows)
        engine.finishClear()

        assertEquals(18 * 2 + 100, engine.score)
        assertEquals(1, engine.lines)
        assertFalse(engine.isGameOver)

        assertEquals(PieceType.I, engine.boardSnapshot()[Board.HEIGHT - 1][9])
    }

    @Test
    fun levelRisesEveryTenLines() {
        assertEquals(1, Scoring.levelFor(9))
        assertEquals(2, Scoring.levelFor(10))
        assertEquals(3, Scoring.levelFor(25))
    }

    @Test
    fun lineClearPointsFollowTable() {
        assertEquals(100, Scoring.lineClearPoints(1, 1))
        assertEquals(600, Scoring.lineClearPoints(2, 2))
        assertEquals(1500, Scoring.lineClearPoints(3, 3))
        assertEquals(3200, Scoring.lineClearPoints(4, 4))
    }

    @Test
    fun dropIntervalShrinksButNotBelowMinimum() {
        assertEquals(1000L, Scoring.dropIntervalMs(1))
        assertEquals(900L, Scoring.dropIntervalMs(2))
        assertEquals(100L, Scoring.dropIntervalMs(60))
    }

    @Test
    fun gameEndsWhenNewPieceOverlapsBlocks() {
        val board = emptyBoard()
        for (y in 0 until 4) for (x in 1 until Board.WIDTH) board[y][x] = PieceType.Z.id
        val engine = GameEngine(
            restoreFrom = snapshot(board, PieceState(PieceType.O.id, 0, 4, 20), next = PieceType.T.id)
        )
        engine.hardDrop()
        assertTrue(engine.isGameOver)
        assertEquals(null, engine.current)
        assertFalse(engine.moveLeft())
    }

    @Test
    fun snapshotRoundTripKeepsState() {
        val engine = GameEngine(Random(7))
        engine.moveLeft(); engine.rotate(); engine.softDrop()
        val snap = engine.snapshot(elapsedSec = 12)
        val restored = GameEngine(Random(8), restoreFrom = snap)
        assertEquals(engine.current, restored.current)
        assertEquals(engine.next, restored.next)
        assertEquals(engine.score, restored.score)
        assertEquals(snap.board, restored.snapshot(12).board)
    }

    @Test
    fun ghostLandsOnFloor() {
        val engine = GameEngine(Random(5))
        val ghost = engine.ghostPiece()!!
        assertEquals(Board.HEIGHT - 1, ghost.cells().maxOf { it.y })
    }
}
