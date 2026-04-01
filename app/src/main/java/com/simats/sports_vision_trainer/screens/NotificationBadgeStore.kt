package com.simats.sports_vision_trainer.screens

import android.content.Context

object NotificationBadgeStore {

    private const val PREF = "notif_badge"
    private const val KEY = "has_new"

    fun setNew(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY, true)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY, false)
            .apply()
    }

    fun hasNew(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY, false)
    }
}
