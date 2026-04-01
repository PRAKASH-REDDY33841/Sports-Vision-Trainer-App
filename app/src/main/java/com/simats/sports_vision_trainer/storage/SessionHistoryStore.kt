package com.simats.sports_vision_trainer.storage

import android.content.Context
import com.simats.sports_vision_trainer.model.GameSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SessionHistoryStore {

    private const val PREF_NAME = "game_history"
    private const val KEY_SESSIONS = "sessions"

    fun saveSession(context: Context, session: GameSession) {

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = loadSessions(context).toMutableList()

        existing.add(session)

        val json = Gson().toJson(existing)
        prefs.edit().putString(KEY_SESSIONS, json).apply()
    }

    fun loadSessions(context: Context): List<GameSession> {

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SESSIONS, null) ?: return emptyList()

        val type = object : TypeToken<List<GameSession>>() {}.type
        return Gson().fromJson(json, type)
    }
}
