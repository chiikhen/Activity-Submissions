package com.example.exercise_2_music_player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

object SharedMusicPlayer {

    private var player: ExoPlayer? = null
    var currentSong: String? = null
        private set

    private fun getOrCreatePlayer(context: Context): ExoPlayer {
        val existing = player
        if (existing != null) {
            return existing
        }

        val created = ExoPlayer.Builder(context.applicationContext).build()
        player = created
        return created
    }

    fun playSong(context: Context, song: String) {
        val exoPlayer = getOrCreatePlayer(context)
        val url = song.substringAfter(" - ")

        if (currentSong != song) {
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            currentSong = song
        } else if (exoPlayer.playbackState == Player.STATE_IDLE) {
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
        }

        exoPlayer.play()
    }

    fun togglePlayPause(context: Context) {
        val exoPlayer = getOrCreatePlayer(context)
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            return
        }

        val song = currentSong
        if (song != null && exoPlayer.playbackState == Player.STATE_IDLE) {
            exoPlayer.setMediaItem(MediaItem.fromUri(song.substringAfter(" - ")))
            exoPlayer.prepare()
        }

        exoPlayer.play()
    }

    fun pause() {
        player?.pause()
    }

    fun isPlaying(): Boolean = player?.isPlaying == true

    fun release() {
        player?.release()
        player = null
        currentSong = null
    }
}
