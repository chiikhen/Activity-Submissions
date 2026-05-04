package com.example.exercise_2_music_player

data class PlayerUiState(
    val song: Song?,
    val status: String,
    val isPlaying: Boolean,
    val isFavorite: Boolean,
    val currentTimeText: String,
    val totalTimeText: String,
    val progress: Int,
    val volumeProgress: Int
)

interface PlayerSurface {
    fun renderPlayerState(state: PlayerUiState)
}
