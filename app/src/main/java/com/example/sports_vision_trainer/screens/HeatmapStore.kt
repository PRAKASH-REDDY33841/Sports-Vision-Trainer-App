package com.example.sports_vision_trainer.screens

import android.content.Context

object HeatmapStore {

    fun save(ctx: Context, points: List<Pair<Float, Float>>) {
        val prefs = ctx.getSharedPreferences("heatmap", Context.MODE_PRIVATE)
        val text = points.joinToString(";") { "${it.first},${it.second}" }
        prefs.edit().putString("last_points", text).apply()
    }

    fun load(ctx: Context): List<Pair<Float, Float>> {
        val prefs = ctx.getSharedPreferences("heatmap", Context.MODE_PRIVATE)
        val raw = prefs.getString("last_points", "") ?: ""
        if (raw.isBlank()) return emptyList()

        return raw.split(";").mapNotNull {
            val p = it.split(",")
            if (p.size == 2) p[0].toFloat() to p[1].toFloat() else null
        }
    }
}
