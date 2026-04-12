package com.example.tabares_exercise_2

data class PlaybackSnapshot(
    val song: Song?,
    val isPlaying: Boolean,
    val currentPosition: Long,
    val duration: Long
)
