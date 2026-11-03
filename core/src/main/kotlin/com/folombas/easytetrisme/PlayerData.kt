package com.folombas.easytetrisme

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

object PlayerData {
    private val prefs: Preferences = Gdx.app.getPreferences("EasyTetrisSave")
    var isDarkTheme: Boolean
        get() = prefs.getBoolean("darkTheme", true)
        set(value) { prefs.putBoolean("darkTheme", value); prefs.flush() }
    var difficulty: Int
        get() = prefs.getInteger("difficulty", 1)
        set(value) { prefs.putInteger("difficulty", value); prefs.flush() }
    var highScore: Int
        get() = prefs.getInteger("highScore", 0)
        set(value) { prefs.putInteger("highScore", value); prefs.flush() }
    var dailyLinesTarget: Int
        get() = prefs.getInteger("dailyTarget", 5)
        set(value) { prefs.putInteger("dailyTarget", value); prefs.flush() }
    var dailyLinesCleared: Int
        get() = prefs.getInteger("dailyCleared", 0)
        set(value) { prefs.putInteger("dailyCleared", value); prefs.flush() }
}
