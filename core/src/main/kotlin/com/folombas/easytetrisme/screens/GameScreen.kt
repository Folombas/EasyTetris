package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
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
import com.folombas.easytetrisme.Tetromino

class GameScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val viewport = FitViewport(480f, 800f)
    private val batch = SpriteBatch()
    private val board = GameBoard()
    private var dropTimer = 0f
    private val dropInterval: Float
    private var isPaused = false; private var isGameOver = false
    private var screenShake = 0f; private var currentScore = 0
    private var clearFlashTimer = 0f
    
    private val font: BitmapFont; private val uiFont: BitmapFont
    private val stage = Stage(viewport); private val pauseTable = Table()
    private val clearSound: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/line_clear.ogg"))
    
    private val blockTextures = mutableMapOf<Color, Texture>()

    init {
        dropInterval = when (PlayerData.difficulty) { 0 -> 1.2f; 1 -> 0.8f; else -> 0.4f }
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        parameter.size = 32; font = generator.generateFont(parameter)
        parameter.size = 24; uiFont = generator.generateFont(parameter)
        generator.dispose()
        
        // Генерируем красивые текстуры блоков
        Tetromino.values().forEach { generateBlockTexture(it.color) }
        generateBlockTexture(Color.WHITE) // Для вспышки

        board.spawnNew(); setupUI()
        
        val gestureDetector = GestureDetector(object : GestureDetector.GestureAdapter() {
            override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
                if (isPaused || isGameOver) return false
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 0) board.move(1, 0) else board.move(-1, 0)
                } else {
                    if (velocityY < -300) { for (i in 1..20) { if (!board.move(0, 1)) break } }
                    else if (velocityY > 300) { board.rotate() }
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

    private fun generateBlockTexture(baseColor: Color) {
        val size = 64
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        pixmap.setColor(baseColor)
        pixmap.fillRectangle(4, 4, size - 8, size - 8) // Основной квадрат
        
        // Градиент (светлее сверху)
        val lightColor = baseColor.cpy().lerp(Color.WHITE, 0.5f)
        pixmap.setColor(lightColor)
        pixmap.fillRectangle(4, size / 2, size - 8, size / 2 - 4)
        
        // Блик (полупрозрачный белый)
        pixmap.setColor(1f, 1f, 1f, 0.3f)
        pixmap.fillRectangle(8, size - 16, size - 16, 6)
        
        // Темная обводка снизу
        pixmap.setColor(baseColor.cpy().lerp(Color.BLACK, 0.4f))
        pixmap.fillRectangle(4, 4, size - 8, 4)
        pixmap.fillRectangle(4, 4, 4, size - 8)
        pixmap.fillRectangle(size - 8, 4, 4, size - 8)
        
        blockTextures[baseColor] = Texture(pixmap)
        pixmap.dispose()
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
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply { setColor(color); fill() }
        return TextButton.TextButtonStyle().apply { up = com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(Texture(pixmap)); font = uiFont }
    }

    override fun render(delta: Float) {
        if (!isPaused && !isGameOver) {
            if (clearFlashTimer > 0) {
                clearFlashTimer -= delta
                if (clearFlashTimer <= 0) board.finishClearLines()
            } else {
                dropTimer += delta
                if (dropTimer >= dropInterval) {
                    dropTimer = 0f
                    if (!board.move(0, 1)) {
                        board.placePiece()
                        if (board.linesJustCleared > 0) {
                            currentScore += board.linesJustCleared * 100
                            screenShake = 0.3f
                            clearFlashTimer = 0.3f
                            clearSound.play(0.5f)
                        }
                        if (board.isGameOver) {
                            isGameOver = true
                            if (currentScore > PlayerData.highScore) PlayerData.highScore = currentScore
                        }
                    }
                }
            }
        }

        if (screenShake > 0) {
            screenShake -= delta
            viewport.camera.position.x = 240f + com.badlogic.gdx.math.MathUtils.random(-8f, 8f)
            viewport.camera.position.y = 400f + com.badlogic.gdx.math.MathUtils.random(-8f, 8f)
        } else { viewport.camera.position.x = 240f; viewport.camera.position.y = 400f }
        viewport.camera.update()

        Gdx.gl.glClearColor(if (PlayerData.isDarkTheme) 0.05f else 0.85f, if (PlayerData.isDarkTheme) 0.05f else 0.85f, if (PlayerData.isDarkTheme) 0.1f else 0.9f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply()

        val blockSize = 32f; val offsetX = (viewport.worldWidth - board.cols * blockSize) / 2f; val offsetY = 120f
        batch.projectionMatrix = viewport.camera.combined
        batch.begin()

        for (r in 0 until board.rows) {
            for (c in 0 until board.cols) {
                val col = board.grid[r][c]
                val drawX = offsetX + c * blockSize
                val drawY = offsetY + (board.rows - 1 - r) * blockSize
                
                if (col != null) {
                    // Если линия сгорает, рисуем белым
                    if (board.clearedRowIndices.contains(r)) {
                        batch.draw(blockTextures[Color.WHITE], drawX, drawY, blockSize, blockSize)
                    } else {
                        batch.draw(blockTextures[col], drawX, drawY, blockSize, blockSize)
                    }
                } else {
                    // Пустая клетка (сетка)
                    batch.setColor(if (PlayerData.isDarkTheme) Color.DARK_GRAY else Color.LIGHT_GRAY)
                    batch.draw(blockTextures[Color.WHITE], drawX + 1, drawY + 1, blockSize - 2, blockSize - 2)
                }
            }
        }
        batch.setColor(Color.WHITE)

        if (!board.isGameOver && clearFlashTimer <= 0) {
            for (r in board.currentShape.indices) for (c in board.currentShape[r].indices) if (board.currentShape[r][c] == 1) {
                batch.draw(blockTextures[board.currentColor], offsetX + (board.currentX + c) * blockSize, offsetY + (board.rows - 1 - (board.currentY + r)) * blockSize, blockSize, blockSize)
            }
        }
        batch.end()

        stage.act(delta); stage.draw()
        batch.begin(); font.setColor(if (PlayerData.isDarkTheme) Color.WHITE else Color.BLACK); font.draw(batch, "СЧЕТ: $currentScore", 20f, 760f); batch.end()
    }

    override fun resize(width: Int, height: Int) { viewport.update(width, height, true) }
    override fun dispose() { batch.dispose(); font.dispose(); stage.dispose(); clearSound.dispose(); blockTextures.values.forEach { it.dispose() } }
}
