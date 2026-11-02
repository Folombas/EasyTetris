package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport

class SettingsScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val stage = Stage(FitViewport(800f, 480f))
    private val font = BitmapFont()

    init {
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val title = Label("НАСТРОЙКИ", Label.LabelStyle(font, Color.WHITE)).apply { setFontScale(2f) }
        
        val diffBtn = TextButton("Сложность: Средняя", createStyle(Color(0.2f, 0.6f, 0.8f, 1f)))
        val themeBtn = TextButton("Тема: Темная", createStyle(Color(0.2f, 0.6f, 0.8f, 1f)))
        val backBtn = TextButton("Назад", createStyle(Color(0.8f, 0.2f, 0.2f, 1f)))

        backBtn.addListener(object : ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                game.screen = MainMenuScreen(game)
            }
        })

        table.add(title).padBottom(50f).row()
        table.add(diffBtn).width(300f).height(60f).pad(10f).row()
        table.add(themeBtn).width(300f).height(60f).pad(10f).row()
        table.add(backBtn).width(300f).height(60f).pad(20f).row()
        Gdx.input.inputProcessor = stage
    }

    private fun createStyle(color: Color): TextButton.TextButtonStyle {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply { setColor(color); fill() }
        val drawable = TextureRegionDrawable(Texture(pixmap))
        return TextButton.TextButtonStyle().apply { up = drawable; font = this@SettingsScreen.font; fontColor = Color.WHITE }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }
    override fun dispose() { stage.dispose(); font.dispose() }
    override fun resize(width: Int, height: Int) { stage.viewport.update(width, height, true) }
}
