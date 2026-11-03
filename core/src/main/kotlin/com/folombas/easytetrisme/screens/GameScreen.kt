package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.folombas.easytetrisme.GameBoard
import com.folombas.easytetrisme.GameSettings

class GameScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val viewport = FitViewport(480f, 800f)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val board = GameBoard()
    private var dropTimer = 0f
    private val dropInterval: Float
    private var touchX = 0f
    private var touchY = 0f
    private var score = 0
    private val font: BitmapFont
    private val stage = Stage(viewport)
    private val uiFont: BitmapFont

    init {
        dropInterval = when (GameSettings.difficulty) {
            0 -> 1.2f
            1 -> 0.8f
            else -> 0.4f
        }

        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 32
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        font = generator.generateFont(parameter)
        
        parameter.size = 24
        uiFont = generator.generateFont(parameter)
        generator.dispose()

        board.spawnNew()
        
        val menuButton = TextButton("МЕНЮ", TextButton.TextButtonStyle().apply {
            up = com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
                com.badlogic.gdx.graphics.Texture(
                    com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888).apply { 
                        setColor(Color.RED); fill() 
                    }
                )
            )
            this.font = uiFont
        })
        menuButton.setPosition(380f, 740f)
        menuButton.setSize(80f, 40f)
        menuButton.addListener(object : ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                game.screen = MainMenuScreen(game)
            }
        })
        stage.addActor(menuButton)
        Gdx.input.inputProcessor = com.badlogic.gdx.InputMultiplexer(stage, object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                touchX = screenX.toFloat(); touchY = screenY.toFloat(); return true
            }
            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (Gdx.input.isTouched(0) && stage.hit(screenX.toFloat(), screenY.toFloat(), true) == null) {
                    val deltaX = screenX - touchX
                    val deltaY = screenY - touchY
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (deltaX > 40) board.move(1, 0)
                        else if (deltaX < -40) board.move(-1, 0)
                    } else {
                        if (deltaY > 40) board.move(0, 1)
                        else if (deltaY < -40) board.rotate()
                    }
                    if (Math.abs(deltaX) < 20 && Math.abs(deltaY) < 20) board.rotate()
                }
                return true
            }
        })
    }

    override fun render(delta: Float) {
        if (!board.isGameOver) {
            dropTimer += delta
            if (dropTimer >= dropInterval) {
                dropTimer = 0f
                if (!board.move(0, 1)) board.placePiece()
            }
        }

        Gdx.gl.glClearColor(
            if (GameSettings.isDarkTheme) 0.05f else 0.85f,
            if (GameSettings.isDarkTheme) 0.05f else 0.85f,
            if (GameSettings.isDarkTheme) 0.1f else 0.9f,
            1f
        )
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        val blockSize = 32f
        val offsetX = (viewport.worldWidth - board.cols * blockSize) / 2f
        val offsetY = 120f

        shapeRenderer.projectionMatrix = viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        for (r in 0 until board.rows) {
            for (c in 0 until board.cols) {
                shapeRenderer.color = board.grid[r][c] ?: if (GameSettings.isDarkTheme) Color.DARK_GRAY else Color.LIGHT_GRAY
                if (board.grid[r][c] == null) {
                    shapeRenderer.rect(offsetX + c * blockSize + 1, offsetY + (board.rows - 1 - r) * blockSize + 1, blockSize - 2, blockSize - 2)
                } else {
                    shapeRenderer.rect(offsetX + c * blockSize, offsetY + (board.rows - 1 - r) * blockSize, blockSize, blockSize)
                }
            }
        }

        if (!board.isGameOver) {
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
        }
        shapeRenderer.end()

        stage.act(delta)
        stage.draw()

        batch.projectionMatrix = viewport.camera.combined
        batch.begin()
        font.setColor(if (GameSettings.isDarkTheme) Color.WHITE else Color.BLACK)
        font.draw(batch, "СЧЕТ: $score", 20f, 760f)
        if (board.isGameOver) {
            font.draw(batch, "ИГРА ОКОНЧЕНА", viewport.worldWidth / 2 - 120, viewport.worldHeight / 2 + 50)
        }
        batch.end()
    }

    override fun resize(width: Int, height: Int) { viewport.update(width, height, true) }
    override fun dispose() { shapeRenderer.dispose(); batch.dispose(); font.dispose(); stage.dispose() }
}
