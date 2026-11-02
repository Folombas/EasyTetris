package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport

class MainMenuScreen : ScreenAdapter() {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("ui/uiskin.json"))
    private val clickSound: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.ogg"))

    init {
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val titleLabel = Label("EASY TETRIS", skin)
        titleLabel.setFontScale(2.5f)
        titleLabel.setAlignment(Align.center)

        val startButton = TextButton("Start Game", skin)
        val collectionButton = TextButton("Collection", skin)
        val quitButton = TextButton("Quit", skin)

        listOf(startButton, collectionButton, quitButton).forEach { button ->
            button.setTransform(true)
            button.setOrigin(Align.center)
            button.color.a = 0f
            button.addAction(fadeIn(0.5f))
        }

        fun animateClick(button: TextButton, action: () -> Unit) {
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

        animateClick(startButton) { Gdx.app.log("Menu", "Start clicked!") }
        animateClick(collectionButton) { Gdx.app.log("Menu", "Collection clicked!") }
        animateClick(quitButton) { Gdx.app.exit() }

        table.add(titleLabel).padBottom(60f).row()
        table.add(startButton).width(250f).height(60f).pad(10f).row()
        table.add(collectionButton).width(250f).height(60f).pad(10f).row()
        table.add(quitButton).width(250f).height(60f).pad(10f)

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
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
        clickSound.dispose()
    }
}
