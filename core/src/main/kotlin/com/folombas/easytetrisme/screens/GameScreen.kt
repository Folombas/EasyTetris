package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.folombas.easytetrisme.GameBoard
import com.folombas.easytetrisme.PlayerData

class GameScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val viewport = FitViewport(480f, 800f)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val board = GameBoard()
    private var dropTimer = 0f
    private val dropInterval: Float
    private var isPaused = false
    private var isGameOver = false
    private val font: BitmapFont
    private val uiFont: BitmapFont
    private val stage = Stage(viewport)
    private val pauseTable = Table()

    init {
        dropInterval = when (PlayerData.difficulty) { 0 -> 1.2f; 1 -> 0.8f; else -> 0.4f }
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        parameter.size = 32
        font = generator.generateFont(parameter)
        parameter.size = 24
        uiFont = generator.generateFont(parameter)
        generator.dispose()

        board.spawnNew()
        setupUI()
        
        val gestureDetector = GestureDetector(object : GestureDetector.GestureAdapter() {
            override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
                if (isPaused || isGameOver) return false
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 0) board.move(1, 0) else board.move(-1, 0)
                } else {
                    if (velocityY < -500) board.move(0, 1)
                    else if (velocityY > 500) board.rotate()
                }
                return true
            }
            override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
                if (!isPaused && !isGameOver && stage.hit(x, y, true) == null) board.rotate()
                return true
            }
        })
        Gdx.input.inputProcessor = com.badlogic.gdx.InputMultiplexer(stage, gestureDetector)
    }

    private fun setupUI() {
        val pauseBtn = TextButton("II", createBtnStyle(Color.ORANGE))
        pauseBtn.setSize(60f, 50f); pauseBtn.setPosition(400f, 730f)
        pauseBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (!isGameOver) { isPaused = true; pauseTable.isVisible = true }
            }
        })
        stage.addActor(pauseBtn)

        pauseTable.setFillParent(true); pauseTable.isVisible = false
        val resumeBtn = TextButton("ПРОДОЛЖИТЬ", createBtnStyle(Color.GREEN))
        val menuBtn = TextButton("В МЕНЮ", createBtnStyle(Color.RED))
        val pauseLabel = Label("ПАУЗА", Label.LabelStyle(font, Color.WHITE))

        resumeBtn.addListener(object : ClickListener() { override fun clicked(event: InputEvent?, x: Float, y: Float) { isPaused = false; pauseTable.isVisible = false } })
        menuBtn.addListener(object : ClickListener() { override fun clicked(event: InputEvent?, x: Float, y: Float) { game.screen = MainMenuScreen(game) } })

        pauseTable.add(pauseLabel).padBottom(50f).row()
        pauseTable.add(resumeBtn).width(250f).height(60f).pad(10f).row()
        pauseTable.add(menuBtn).width(250f).height(60f).pad(10f)
        stage.addActor(pauseTable)
    }

    private fun createBtnStyle(color: Color): TextButton.TextButtonStyle {
        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888).apply { setColor(color); fill() }
        return TextButton.TextButtonStyle().apply { up = com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(com.badlogic.gdx.graphics.Texture(pixmap)); font = uiFont }
    }

    override fun render(delta: Float) {
        if (!isPaused && !isGameOver) {
            dropTimer += delta
            if (dropTimer >= dropInterval) {
                dropTimer = 0f
                if (!board.move(0, 1)) {
                    board.placePiece()
                    if (board.isGameOver) {
                        isGameOver = true
                        if (PlayerData.highScore < 100) PlayerData.highScore = 100 // Временный рекорд
                    }
                }
            }
        }

        Gdx.gl.glClearColor(if (PlayerData.isDarkTheme) 0.05f else 0.85f, if (PlayerData.isDarkTheme) 0.05f else 0.85f, if (PlayerData.isDarkTheme) 0.1f else 0.9f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        val blockSize = 32f
        val offsetX = (viewport.worldWidth - board.cols * blockSize) / 2f
        val offsetY = 120f

        shapeRenderer.projectionMatrix = viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (r in 0 until board.rows) {
            for (c in 0 until board.cols) {
                shapeRenderer.color = board.grid[r][c] ?: if (PlayerData.isDarkTheme) Color.DARK_GRAY else Color.LIGHT_GRAY
                if (board.grid[r][c] == null) shapeRenderer.rect(offsetX + c * blockSize + 1, offsetY + (board.rows - 1 - r) * blockSize + 1, blockSize - 2, blockSize - 2)
                else shapeRenderer.rect(offsetX + c * blockSize, offsetY + (board.rows - 1 - r) * blockSize, blockSize, blockSize)
            }
        }
        if (!board.isGameOver) {
            shapeRenderer.color = board.currentColor
            for (r in board.currentShape.indices) {
                for (c in board.currentShape[r].indices) {
                    if (board.currentShape[r][c] == 1) shapeRenderer.rect(offsetX + (board.currentX + c) * blockSize, offsetY + (board.rows - 1 - (board.currentY + r)) * blockSize, blockSize, blockSize)
                }
            }
        }
        shapeRenderer.end()
        stage.act(delta); stage.draw()

        batch.projectionMatrix = viewport.camera.combined
        batch.begin()
        font.setColor(if (PlayerData.isDarkTheme) Color.WHITE else Color.BLACK)
        font.draw(batch, "СЧЕТ: 0", 20f, 760f)
        batch.end()
    }
    override fun resize(width: Int, height: Int) { viewport.update(width, height, true) }
    override fun dispose() { shapeRenderer.dispose(); batch.dispose(); font.dispose(); stage.dispose() }
}
