package com.example.sports_vision_trainer.screens

import android.content.Context

object SettingsStore {

    private const val PREF = "svt_settings"

    var darkMode = false
    var audioEnabled = true
    var hapticEnabled = true

    fun load(ctx: Context) {
        val p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        darkMode = p.getBoolean("dark", false)
        audioEnabled = p.getBoolean("audio", true)
        hapticEnabled = p.getBoolean("haptic", true)
    }

    fun save(ctx: Context) {
        val p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        p.edit()
            .putBoolean("dark", darkMode)
            .putBoolean("audio", audioEnabled)
            .putBoolean("haptic", hapticEnabled)
            .apply()
    }
}
