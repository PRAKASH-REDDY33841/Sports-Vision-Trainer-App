package com.example.sports_vision_trainer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sports_vision_trainer.model.GameSession
import kotlin.math.pow

class StatsViewModel : ViewModel() {

    fun calculateAverageReaction(sessions: List<GameSession>): Double {
        return if (sessions.isNotEmpty())
            sessions.map { it.avgReaction }.average()
        else 0.0
    }

    fun calculateAccuracy(sessions: List<GameSession>): Double {
        if (sessions.isEmpty()) return 0.0
        return 1 - (sessions.sumOf { it.wrong }.toDouble() /
                (sessions.size * 10.0))
    }

    // 🔥 Real AI Trend Slope (Linear Regression)
    fun calculateTrendSlope(values: List<Float>): Float {

        if (values.size < 2) return 0f

        val n = values.size
        val xValues = (0 until n).map { it.toFloat() }

        val meanX = xValues.average()
        val meanY = values.average()

        var numerator = 0.0
        var denominator = 0.0

        for (i in values.indices) {
            numerator += (xValues[i] - meanX) * (values[i] - meanY)
            denominator += (xValues[i] - meanX).pow(2)
        }

        return if (denominator == 0.0) 0f
        else (numerator / denominator).toFloat()
    }
}
