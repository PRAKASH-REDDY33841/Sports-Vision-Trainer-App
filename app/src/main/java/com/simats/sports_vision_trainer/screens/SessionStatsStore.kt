package com.simats.sports_vision_trainer.screens

import android.content.Context

object SessionStatsStore {

    private const val PREF = "session_stats"

    private const val KEY_LAST_HITS = "last_hits"
    private const val KEY_LAST_AVG = "last_avg"

    // ✅ NEW — best performance tracking
    private const val KEY_BEST_HITS = "best_hits"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    // ---------- SAVE LAST SESSION ----------
    fun save(context: Context, hits: Int, avg: Long) {
        prefs(context).edit()
            .putInt(KEY_LAST_HITS, hits)
            .putLong(KEY_LAST_AVG, avg)
            .apply()
    }

    // ---------- LOAD LAST SESSION ----------
    fun load(context: Context): Pair<Int, Long> {
        val sp = prefs(context)
        val hits = sp.getInt(KEY_LAST_HITS, -1)
        val avg = sp.getLong(KEY_LAST_AVG, -1)
        return hits to avg
    }

    // ---------- BEST HITS ----------
    fun loadBestHits(context: Context): Int =
        prefs(context).getInt(KEY_BEST_HITS, 0)

    fun saveBestHits(context: Context, hits: Int) {
        val best = loadBestHits(context)

        if (hits > best) {
            prefs(context).edit()
                .putInt(KEY_BEST_HITS, hits)
                .apply()
        }
    }
}
