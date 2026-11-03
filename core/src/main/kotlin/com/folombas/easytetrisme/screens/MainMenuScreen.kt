package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.folombas.easytetrisme.GameSettings

class MainMenuScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val stage = Stage(FitViewport(480f, 800f))
    private val clickSound: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.ogg"))
    private lateinit var font: BitmapFont

    init {
        generateFont()
        val table = Table().apply { setFillParent(true) }
        stage.addActor(table)

        val titleColor = if (GameSettings.isDarkTheme) Color.CYAN else Color(0.1f, 0.4f, 0.8f, 1f)
        val titleLabel = Label("ТЕТРИС", Label.LabelStyle(font, titleColor)).apply {
            setFontScale(2.5f)
            setAlignment(Align.center)
        }

        val startButton = TextButton("ИГРАТЬ", createModernStyle(if (GameSettings.isDarkTheme) Color(0.2f, 0.7f, 0.3f, 1f) else Color(0.1f, 0.6f, 0.2f, 1f)))
        val settingsButton = TextButton("НАСТРОЙКИ", createModernStyle(if (GameSettings.isDarkTheme) Color(0.2f, 0.4f, 0.8f, 1f) else Color(0.1f, 0.3f, 0.7f, 1f)))
        val quitButton = TextButton("ВЫХОД", createModernStyle(if (GameSettings.isDarkTheme) Color(0.8f, 0.2f, 0.2f, 1f) else Color(0.7f, 0.1f, 0.1f, 1f)))

        listOf(startButton, settingsButton, quitButton).forEach {
            it.color.a = 0f
            it.addAction(fadeIn(0.8f))
        }

        fun bindClick(button: TextButton, action: () -> Unit) {
            button.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    clickSound.play()
                    button.clearActions()
                    button.addAction(sequence(scaleTo(0.95f, 0.95f, 0.05f), scaleTo(1f, 1f, 0.05f), delay(0.1f), com.badlogic.gdx.scenes.scene2d.actions.Actions.run { action() }))
                }
            })
        }

        bindClick(startButton) { game.screen = GameScreen(game) }
        bindClick(settingsButton) { game.screen = SettingsScreen(game) }
        bindClick(quitButton) { Gdx.app.exit() }

        table.add(titleLabel).padBottom(100f).row()
        table.add(startButton).width(320f).height(80f).pad(15f).row()
        table.add(settingsButton).width(320f).height(80f).pad(15f).row()
        table.add(quitButton).width(320f).height(80f).pad(15f)

        Gdx.input.inputProcessor = stage
    }

    private fun generateFont() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 48
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        font = generator.generateFont(parameter)
        generator.dispose()
    }

    private fun createModernStyle(color: Color): TextButton.TextButtonStyle {
        val pixmap = Pixmap(256, 80, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fillRectangle(20, 0, 216, 80)
        pixmap.fillCircle(20, 40, 40)
        pixmap.fillCircle(236, 40, 40)
        return TextButton.TextButtonStyle().apply {
            up = TextureRegionDrawable(Texture(pixmap))
            font = this@MainMenuScreen.font
            fontColor = Color.WHITE
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(
            if (GameSettings.isDarkTheme) 0.08f else 0.9f,
            if (GameSettings.isDarkTheme) 0.08f else 0.9f,
            if (GameSettings.isDarkTheme) 0.12f else 0.95f,
            1f
        )
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }
    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun dispose() { stage.dispose(); font.dispose(); clickSound.dispose() }
}
