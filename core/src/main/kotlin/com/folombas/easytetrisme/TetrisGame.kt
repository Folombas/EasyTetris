package com.folombas.easytetrisme

import com.badlogic.gdx.Game
import com.folombas.easytetrisme.screens.MainMenuScreen

class TetrisGame : Game() {
    override fun create() {
        setScreen(MainMenuScreen(this))
    }
}
