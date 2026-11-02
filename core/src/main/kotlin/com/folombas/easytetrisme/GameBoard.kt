package com.folombas.easytetrisme

import com.badlogic.gdx.graphics.Color

class GameBoard {
    val cols = 10
    val rows = 20
    val grid = Array(rows) { arrayOfNulls<Color>(cols) }
    
    var currentX = 3
    var currentY = 0
    var currentShape = Tetromino.I.shape
    var currentColor = Tetromino.I.color

    fun spawnNew() {
        val randomType = Tetromino.values().random()
        currentShape = randomType.shape
        currentColor = randomType.color
        currentX = cols / 2 - currentShape[0].size / 2
        currentY = 0
    }

    fun isValidMove(x: Int, y: Int, shape: Array<IntArray>): Boolean {
        for (r in shape.indices) {
            for (c in shape[r].indices) {
                if (shape[r][c] == 1) {
                    val newX = x + c
                    val newY = y + r
                    if (newX < 0 || newX >= cols || newY >= rows) return false
                    if (newY >= 0 && grid[newY][newX] != null) return false
                }
            }
        }
        return true
    }

    fun move(dx: Int, dy: Int): Boolean {
        if (isValidMove(currentX + dx, currentY + dy, currentShape)) {
            currentX += dx
            currentY += dy
            return true
        }
        return false
    }

    fun rotate() {
        val rotated = Tetromino.values().first { it.shape === currentShape }.rotated()
        if (isValidMove(currentX, currentY, rotated)) {
            currentShape = rotated
        }
    }

    fun placePiece() {
        for (r in currentShape.indices) {
            for (c in currentShape[r].indices) {
                if (currentShape[r][c] == 1) {
                    if (currentY + r >= 0) {
                        grid[currentY + r][currentX + c] = currentColor
                    }
                }
            }
        }
        clearLines()
        spawnNew()
    }

    private fun clearLines() {
        for (r in rows - 1 downTo 0) {
            var full = true
            for (c in 0 until cols) {
                if (grid[r][c] == null) {
                    full = false
                    break
                }
            }
            if (full) {
                for (row in r downTo 1) {
                    grid[row] = grid[row - 1].clone()
                }
                grid[0] = arrayOfNulls(cols)
            }
        }
    }
}
