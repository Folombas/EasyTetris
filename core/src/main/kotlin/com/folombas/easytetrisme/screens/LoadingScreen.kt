package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.folombas.easytetrisme.PlayerData

class LoadingScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val stage = Stage(FitViewport(480f, 800f))
    private var timePassed = 0f

    init {
        PlayerData.updateLoginStreak()
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Bold.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        parameter.size = 60
        parameter.color = Color.CYAN
        val font = generator.generateFont(parameter)
        generator.dispose()
        val table = Table().apply { setFillParent(true) }
        stage.addActor(table)
        val title = Label("ТЕТРИС", Label.LabelStyle(font, Color.CYAN)).apply { 
            setAlignment(Align.center) 
            color.a = 0f
            addAction(Actions.fadeIn(0.5f))
        }
        table.add(title)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
        timePassed += delta
        if (timePassed >= 1.5f) {
            game.screen = MainMenuScreen(game)
        }
    }
    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
}
