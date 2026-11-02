package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.folombas.easytetrisme.GameBoard

class GameScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val viewport = FitViewport(360f, 640f)
    private val shapeRenderer = ShapeRenderer()
    private val board = GameBoard()
    private var dropTimer = 0f
    private val dropInterval = 0.8f

    private var touchX = 0f
    private var touchY = 0f

    init {
        board.spawnNew()
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                touchX = screenX.toFloat()
                touchY = screenY.toFloat()
                return true
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val deltaX = screenX - touchX
                val deltaY = screenY - touchY

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX > 50) board.move(1, 0)
                    else if (deltaX < -50) board.move(-1, 0)
                } else {
                    if (deltaY > 50) board.move(0, 1)
                    else if (deltaY < -50) board.rotate()
                }
                
                // Короткое нажатие = поворот
                if (Math.abs(deltaX) < 20 && Math.abs(deltaY) < 20) {
                    board.rotate()
                }
                return true
            }
        }
    }

    override fun render(delta: Float) {
        dropTimer += delta
        if (dropTimer >= dropInterval) {
            dropTimer = 0f
            if (!board.move(0, 1)) {
                board.placePiece()
            }
        }

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        shapeRenderer.projectionMatrix = viewport.camera.combined
        
        val blockSize = 30f
        val offsetX = (viewport.worldWidth - board.cols * blockSize) / 2f
        val offsetY = 20f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Рисуем сетку и упавшие блоки
        for (r in 0 until board.rows) {
            for (c in 0 until board.cols) {
                shapeRenderer.color = board.grid[r][c] ?: Color.DARK_GRAY
                if (board.grid[r][c] == null) {
                    shapeRenderer.rect(offsetX + c * blockSize + 1, offsetY + (board.rows - 1 - r) * blockSize + 1, blockSize - 2, blockSize - 2)
                } else {
                    shapeRenderer.rect(offsetX + c * blockSize, offsetY + (board.rows - 1 - r) * blockSize, blockSize, blockSize)
                }
            }
        }

        // Рисуем падающую фигуру
        shapeRenderer.color = board.currentColor
        for (r in board.currentShape.indices) {
            for (c in board.currentShape[r].indices) {
                if (board.currentShape[r][c] == 1) {
                    shapeRenderer.rect(
                        offsetX + (board.currentX + c) * blockSize,
                        offsetY + (board.rows - 1 - (board.currentY + r)) * blockSize,
                        blockSize, blockSize
                    )
                }
            }
        }
        shapeRenderer.end()
    }

    override fun resize(width: Int, height: Int) { viewport.update(width, height, true) }
    override fun dispose() { shapeRenderer.dispose() }
}
