package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.folombas.easytetrisme.GameSettings

class SettingsScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val stage = Stage(FitViewport(480f, 800f))
    private lateinit var font: BitmapFont
    private lateinit var diffBtn: TextButton
    private lateinit var themeBtn: TextButton

    init {
        generateFont()
        val table = Table().apply { setFillParent(true) }
        stage.addActor(table)

        val textColor = if (GameSettings.isDarkTheme) Color.WHITE else Color.BLACK
        val title = Label("НАСТРОЙКИ", Label.LabelStyle(font, textColor)).apply { setAlignment(Align.center) }
        
        diffBtn = TextButton(getDiffText(), createStyle(Color(0.2f, 0.6f, 0.8f, 1f)))
        themeBtn = TextButton(getThemeText(), createStyle(Color(0.2f, 0.6f, 0.8f, 1f)))
        val backBtn = TextButton("НАЗАД", createStyle(Color(0.8f, 0.2f, 0.2f, 1f)))

        diffBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                GameSettings.difficulty = (GameSettings.difficulty + 1) % 3
                diffBtn.setText(getDiffText())
            }
        })

        themeBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                GameSettings.isDarkTheme = !GameSettings.isDarkTheme
                game.screen = SettingsScreen(game)
            }
        })

        backBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) { game.screen = MainMenuScreen(game) }
        })

        table.add(title).padBottom(60f).row()
        table.add(diffBtn).width(350f).height(80f).pad(15f).row()
        table.add(themeBtn).width(350f).height(80f).pad(15f).row()
        table.add(backBtn).width(350f).height(80f).pad(40f).row()
        Gdx.input.inputProcessor = stage
    }

    private fun getDiffText() = when (GameSettings.difficulty) {
        0 -> "Сложность: Легко"
        1 -> "Сложность: Средне"
        else -> "Сложность: Сложно"
    }

    private fun getThemeText() = if (GameSettings.isDarkTheme) "Тема: Тёмная" else "Тема: Светлая"

    private fun generateFont() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 36
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        font = generator.generateFont(parameter)
        generator.dispose()
    }

    private fun createStyle(color: Color): TextButton.TextButtonStyle {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply { setColor(color); fill() }
        return TextButton.TextButtonStyle().apply { 
            up = TextureRegionDrawable(Texture(pixmap))
            font = this@SettingsScreen.font
            fontColor = Color.WHITE 
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(
            if (GameSettings.isDarkTheme) 0.1f else 0.9f,
            if (GameSettings.isDarkTheme) 0.1f else 0.9f,
            if (GameSettings.isDarkTheme) 0.15f else 0.95f,
            1f
        )
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }
    override fun dispose() { stage.dispose(); font.dispose() }
    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
}
