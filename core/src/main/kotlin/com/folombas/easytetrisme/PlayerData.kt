package com.folombas.easytetrisme

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import java.util.Calendar
import java.util.concurrent.TimeUnit

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
    var loginStreak: Int
        get() = prefs.getInteger("loginStreak", 0)
        private set(value) { prefs.putInteger("loginStreak", value); prefs.flush() }
    private var lastLoginDate: Long
        get() = prefs.getLong("lastLoginDate", 0L)
        set(value) { prefs.putLong("lastLoginDate", value); prefs.flush() }

    fun updateLoginStreak() {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        if (lastLoginDate == 0L) {
            loginStreak = 1
        } else {
            val daysDiff = TimeUnit.MILLISECONDS.toDays(today - lastLoginDate)
            if (daysDiff == 1L) loginStreak += 1
            else if (daysDiff > 1L) loginStreak = 1
        }
        lastLoginDate = today
    }
}
