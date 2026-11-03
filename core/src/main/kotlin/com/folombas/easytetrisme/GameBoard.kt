package com.folombas.easytetrisme

import com.badlogic.gdx.graphics.Color

class GameBoard {
    val cols = 10
    val rows = 20
    val grid = Array(rows) { arrayOfNulls<Color>(cols) }
    var currentX = 3; var currentY = 0
    var currentShape = Tetromino.I.shape
    var currentColor = Tetromino.I.color
    var isGameOver = false
    var linesJustCleared = 0
    var clearedRowIndices = mutableListOf<Int>()

    fun spawnNew() {
        val randomType = Tetromino.values().random()
        currentShape = randomType.shape.map { it.clone() }.toTypedArray()
        currentColor = randomType.color
        currentX = cols / 2 - currentShape[0].size / 2; currentY = 0
        if (!isValidMove(currentX, currentY, currentShape)) isGameOver = true
    }

    fun isValidMove(x: Int, y: Int, shape: Array<IntArray>): Boolean {
        for (r in shape.indices) for (c in shape[r].indices) if (shape[r][c] == 1) {
            val newX = x + c; val newY = y + r
            if (newX < 0 || newX >= cols || newY >= rows) return false
            if (newY >= 0 && grid[newY][newX] != null) return false
        }
        return true
    }

    fun move(dx: Int, dy: Int): Boolean {
        if (isGameOver) return false
        if (isValidMove(currentX + dx, currentY + dy, currentShape)) {
            currentX += dx; currentY += dy; return true
        }
        return false
    }

    fun rotate() {
        if (isGameOver) return
        val rows = currentShape.size; val cols = currentShape[0].size
        val rotated = Array(cols) { IntArray(rows) }
        for (r in 0 until rows) for (c in 0 until cols) rotated[c][rows - 1 - r] = currentShape[r][c]
        if (isValidMove(currentX, currentY, rotated)) currentShape = rotated
    }

    fun placePiece() {
        if (isGameOver) return
        for (r in currentShape.indices) for (c in currentShape[r].indices) if (currentShape[r][c] == 1) {
            if (currentY + r >= 0) grid[currentY + r][currentX + c] = currentColor
        }
        findFullLines()
        if (clearedRowIndices.isEmpty()) spawnNew()
    }

    private fun findFullLines() {
        clearedRowIndices.clear()
        for (r in 0 until rows) {
            var full = true
            for (c in 0 until cols) if (grid[r][c] == null) { full = false; break }
            if (full) clearedRowIndices.add(r)
        }
        linesJustCleared = clearedRowIndices.size
    }

    fun finishClearLines() {
        for (r in clearedRowIndices.sortedDescending()) {
            for (row in r downTo 1) grid[row] = grid[row - 1].clone()
            grid[0] = arrayOfNulls(cols)
        }
        clearedRowIndices.clear()
        spawnNew()
    }
}
