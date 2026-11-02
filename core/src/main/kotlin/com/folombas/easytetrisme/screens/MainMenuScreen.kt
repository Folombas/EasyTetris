package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
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

class MainMenuScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val stage = Stage(FitViewport(800f, 480f))
    private val font = BitmapFont().apply { data.setScale(1.5f) }
    private val clickSound: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.ogg"))

    init {
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val titleLabel = Label("EASY TETRIS", Label.LabelStyle(font, Color.CYAN)).apply { 
            setFontScale(2.5f)
            setAlignment(Align.center) 
        }

        val startButton = TextButton("ИГРАТЬ", createModernStyle(Color(0.2f, 0.8f, 0.4f, 1f)))
        val settingsButton = TextButton("НАСТРОЙКИ", createModernStyle(Color(0.3f, 0.5f, 0.9f, 1f)))
        val quitButton = TextButton("ВЫХОД", createModernStyle(Color(0.9f, 0.3f, 0.3f, 1f)))

        listOf(startButton, settingsButton, quitButton).forEach {
            it.color.a = 0f
            it.addAction(fadeIn(0.5f))
        }

        fun bindClick(button: TextButton, action: () -> Unit) {
            button.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    clickSound.play()
                    button.clearActions()
                    button.addAction(sequence(
                        scaleTo(0.9f, 0.9f, 0.05f),
                        scaleTo(1f, 1f, 0.05f),
                        delay(0.1f),
                        com.badlogic.gdx.scenes.scene2d.actions.Actions.run { action() }
                    ))
                }
            })
        }

        bindClick(startButton) { game.screen = GameScreen(game) }
        bindClick(settingsButton) { game.screen = SettingsScreen(game) }
        bindClick(quitButton) { Gdx.app.exit() }

        table.add(titleLabel).padBottom(60f).row()
        table.add(startButton).width(350f).height(70f).pad(15f).row()
        table.add(settingsButton).width(350f).height(70f).pad(15f).row()
        table.add(quitButton).width(350f).height(70f).pad(15f)

        Gdx.input.inputProcessor = stage
    }

    private fun createModernStyle(color: Color): TextButton.TextButtonStyle {
        val pixmap = Pixmap(64, 64, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        // Рисуем круглую кнопку
        pixmap.fillCircle(32, 32, 32)
        val drawable = TextureRegionDrawable(Texture(pixmap))
        
        val pressedPixmap = Pixmap(64, 64, Pixmap.Format.RGBA8888)
        pressedPixmap.setColor(color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, 1f)
        pressedPixmap.fillCircle(32, 32, 32)
        val pressedDrawable = TextureRegionDrawable(Texture(pressedPixmap))

        return TextButton.TextButtonStyle().apply {
            up = drawable
            down = pressedDrawable
            font = this@MainMenuScreen.font
            fontColor = Color.WHITE
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }
    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun dispose() { stage.dispose(); font.dispose(); clickSound.dispose() }
}
