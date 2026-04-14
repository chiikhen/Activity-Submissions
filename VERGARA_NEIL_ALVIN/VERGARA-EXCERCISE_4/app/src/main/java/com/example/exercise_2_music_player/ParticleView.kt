package com.example.exercise_2_music_player

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.random.Random

class ParticleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint().apply {
        color = Color.parseColor("#1DB954")
        isAntiAlias = true
    }
    private var progress = 0f

    data class Particle(
        val startX: Float, val startY: Float,
        val targetX: Float, val targetY: Float,
        val size: Float
    )

    fun startAnimation() {
        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2

        for (i in 0..100) {
            particles.add(Particle(
                centerX, centerY,
                Random.nextFloat() * width,
                Random.nextFloat() * height,
                Random.nextInt(5, 15).toFloat()
            ))
        }

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (particles.isEmpty()) return

        for (particle in particles) {
            val x = particle.startX + (particle.targetX - particle.startX) * progress
            val y = particle.startY + (particle.targetY - particle.startY) * progress
            val alpha = (255 * (1 - progress)).toInt()
            paint.alpha = alpha
            canvas.drawCircle(x, y, particle.size, paint)
        }
    }
}