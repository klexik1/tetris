package com.khalchukov.tetris.game

import kotlin.random.Random

class PieceBag(private val random: Random) {
    private val queue = ArrayDeque<PieceType>()

    fun next(): PieceType {
        if (queue.isEmpty()) queue.addAll(PieceType.values().toList().shuffled(random))
        return queue.removeFirst()
    }
}
