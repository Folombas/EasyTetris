package com.folombas.easytetrisme

import com.badlogic.gdx.graphics.Color

enum class Tetromino(val shape: Array<IntArray>, val color: Color) {
    I(arrayOf(intArrayOf(1, 1, 1, 1)), Color.CYAN),
    O(arrayOf(intArrayOf(1, 1), intArrayOf(1, 1)), Color.YELLOW),
    T(arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)), Color.MAGENTA),
    S(arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1, 0)), Color.GREEN),
    Z(arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 1)), Color.RED),
    J(arrayOf(intArrayOf(1, 0, 0), intArrayOf(1, 1, 1)), Color.BLUE),
    L(arrayOf(intArrayOf(0, 0, 1), intArrayOf(1, 1, 1)), Color.ORANGE);
}
