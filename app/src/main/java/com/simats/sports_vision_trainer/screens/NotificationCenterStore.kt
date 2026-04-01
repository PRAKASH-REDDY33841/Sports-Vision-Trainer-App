package com.simats.sports_vision_trainer.screens

import android.content.Context

object NotificationCenterStore {

    private const val PREF = "notif_center"
    private const val KEY = "items"

    fun add(context: Context, msg: String) {
        val p = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val old = p.getString(KEY, "") ?: ""
        val updated = msg + "||" + old
        p.edit().putString(KEY, updated).apply()
    }

    fun load(context: Context): List<String> {
        val raw = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split("||").filter { it.isNotBlank() }
    }
}
