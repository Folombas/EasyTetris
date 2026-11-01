package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick

class MainMenuScreen : ScreenAdapter() {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("ui/uiskin.json"))

    init {
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val titleLabel = Label("EASY TETRIS", skin)
        titleLabel.setFontScale(2f)

        val startButton = TextButton("Start Game", skin)
        val collectionButton = TextButton("Collection", skin)
        val quitButton = TextButton("Quit", skin)

        startButton.onClick {
            Gdx.app.log("Menu", "Start clicked!")
        }

        quitButton.onClick {
            Gdx.app.exit()
        }

        table.add(titleLabel).padBottom(50f).row()
        table.add(startButton).width(200f).pad(10f).row()
        table.add(collectionButton).width(200f).pad(10f).row()
        table.add(quitButton).width(200f).pad(10f)

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
