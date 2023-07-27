package com.flappy.game.domain.game

data class Column(
    var position: Pair<Float, Float> = 0f to 0f,
    val isTop: Boolean
)