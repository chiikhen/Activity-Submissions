package com.example.exercise_2_music_player

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin

class WaveformView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint().apply {
        strokeWidth = 5f
        isAntiAlias = true
        color = 0xFF1DB954.toInt()
    }

    private var isPlaying = false
    private var animationTime = 0f
    private val waveData = FloatArray(40) { (0.3f + Math.random() * 0.7f).toFloat() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val barWidth = width / waveData.size.toFloat()
        val maxHeight = height * 0.4f

        for (i in waveData.indices) {
            val x = i * barWidth + barWidth / 2
            val baseHeight = waveData[i] * maxHeight

            val animatedHeight = if (isPlaying) {
                baseHeight * (0.5f + 0.5f * sin((animationTime + i * 0.2f) * 0.08f).toFloat())
            } else {
                baseHeight * 0.6f
            }

            val y1 = centerY - animatedHeight / 2
            val y2 = centerY + animatedHeight / 2

            canvas.drawLine(x, y1, x, y2, paint)
        }

        if (isPlaying) {
            animationTime += 1f
            invalidate()
        }
    }

    fun setIsPlaying(playing: Boolean) {
        isPlaying = playing
        if (playing) invalidate()
    }
}