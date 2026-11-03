package com.folombas.easytetrisme.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.folombas.easytetrisme.PlayerData

class LoadingScreen(private val game: com.badlogic.gdx.Game) : ScreenAdapter() {
    private val viewport = FitViewport(480f, 800f)
    private val shapeRenderer = ShapeRenderer()
    private var progress = 0f
    private var timePassed = 0f

    init { PlayerData.updateLoginStreak() }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        timePassed += delta
        progress = (timePassed / 2.0f).coerceAtMost(1f) // Загрузка за 2 секунды

        viewport.apply()
        shapeRenderer.projectionMatrix = viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Фон прогресс-бара
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(40f, 350f, 400f, 30f)
        
        // Заполнение
        shapeRenderer.color = Color.CYAN
        shapeRenderer.rect(40f, 350f, 400f * progress, 30f)
        
        shapeRenderer.end()

        if (progress >= 1f) game.screen = MainMenuScreen(game)
    }
    override fun resize(width: Int, height: Int) { viewport.update(width, height, true) }
    override fun dispose() { shapeRenderer.dispose() }
}
