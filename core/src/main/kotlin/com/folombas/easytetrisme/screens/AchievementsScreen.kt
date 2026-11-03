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
import com.folombas.easytetrisme.PlayerData

class AchievementsScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val stage = Stage(FitViewport(480f, 800f))
    private lateinit var font: BitmapFont

    init {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        parameter.size = 36
        font = generator.generateFont(parameter)
        generator.dispose()

        val table = Table().apply { setFillParent(true) }
        stage.addActor(table)
        val textColor = if (PlayerData.isDarkTheme) Color.WHITE else Color.BLACK
        val title = Label("ДОСТИЖЕНИЯ", Label.LabelStyle(font, textColor))
        val highScore = Label("Рекорд: ${PlayerData.highScore}", Label.LabelStyle(font, Color.GOLD))
        val daily = Label("Дейлик: Собери ${PlayerData.dailyLinesTarget} линий", Label.LabelStyle(font, Color.CYAN))
        
        val backBtn = TextButton("НАЗАД", createStyle(Color(0.8f, 0.2f, 0.2f, 1f)))
        backBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) { game.screen = MainMenuScreen(game) }
        })
        table.add(title).padBottom(40f).row()
        table.add(highScore).padBottom(20f).row()
        table.add(daily).padBottom(60f).row()
        table.add(backBtn).width(250f).height(60f)
        Gdx.input.inputProcessor = stage
    }

    private fun createStyle(color: Color): TextButton.TextButtonStyle {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply { setColor(color); fill() }
        return TextButton.TextButtonStyle().apply { up = TextureRegionDrawable(Texture(pixmap)); font = this@AchievementsScreen.font }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(if (PlayerData.isDarkTheme) 0.1f else 0.9f, if (PlayerData.isDarkTheme) 0.1f else 0.9f, if (PlayerData.isDarkTheme) 0.15f else 0.95f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta); stage.draw()
    }
    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
    override fun dispose() { stage.dispose(); font.dispose() }
}
