package com.simats.sports_vision_trainer.model

data class GameSession(
    val gameType: String,
    val score: Int,
    val avgReaction: Long,
    val wrong: Int,
    val timestamp: Long
)
