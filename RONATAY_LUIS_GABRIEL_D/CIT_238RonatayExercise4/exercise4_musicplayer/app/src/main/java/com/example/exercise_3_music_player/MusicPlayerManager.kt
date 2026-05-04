package com.example.exercise_2_music_player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

/**
 * Singleton that keeps the ExoPlayer alive across fragment/navigation changes
 * so music continues playing when switching screens.
 */
object MusicPlayerManager {

    private var player: ExoPlayer? = null
    var currentMusic: Music? = null
        private set

    fun getPlayer(context: Context): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context.applicationContext).build()
        }
        return player!!
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }

    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0
    }

    fun playMusic(context: Context, music: Music) {
        currentMusic = music
        val exo = getPlayer(context)
        exo.stop()
        exo.setMediaItem(MediaItem.fromUri(music.url))
        exo.prepare()
        exo.seekTo(0)
        exo.playWhenReady = true
    }

    fun release() {
        player?.release()
        player = null
        currentMusic = null
    }
}
