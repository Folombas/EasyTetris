package com.folombas.easytetrisme

import com.badlogic.gdx.Game
import com.folombas.easytetrisme.screens.LoadingScreen

class TetrisGame : Game() {
    override fun create() {
        setScreen(LoadingScreen(this))
    }
}
