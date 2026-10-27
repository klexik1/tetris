package com.khalchukov.tetris.data.save

import com.khalchukov.tetris.game.GameSnapshot
import com.khalchukov.tetris.game.PieceState
import org.json.JSONArray
import org.json.JSONObject

object SnapshotJson {
    private const val VERSION = 1

    fun toJson(s: GameSnapshot): String {
        val root = JSONObject()
        root.put("version", VERSION)
        root.put("board", JSONArray(s.board.map { row -> JSONArray(row) }))
        root.put(
            "current",
            s.current?.let {
                JSONObject()
                    .put("type", it.type)
                    .put("rotation", it.rotation)
                    .put("x", it.x)
                    .put("y", it.y)
            } ?: JSONObject.NULL
        )
        root.put("next", s.next)
        root.put("score", s.score)
        root.put("level", s.level)
        root.put("lines", s.lines)
        root.put("elapsedSec", s.elapsedSec)
        return root.toString()
    }

    fun fromJson(text: String): GameSnapshot {
        val o = JSONObject(text)
        val boardJson = o.getJSONArray("board")
        val board = List(boardJson.length()) { r ->
            val row = boardJson.getJSONArray(r)
            List(row.length()) { c -> row.getInt(c) }
        }
        val current = if (o.isNull("current")) {
            null
        } else {
            val c = o.getJSONObject("current")
            PieceState(c.getInt("type"), c.getInt("rotation"), c.getInt("x"), c.getInt("y"))
        }
        return GameSnapshot(
            board = board,
            current = current,
            next = o.getInt("next"),
            score = o.getInt("score"),
            level = o.getInt("level"),
            lines = o.getInt("lines"),
            elapsedSec = o.optInt("elapsedSec", 0),
        )
    }
}
