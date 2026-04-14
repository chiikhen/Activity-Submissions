package com.example.exercise_2_music_player

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var splashContainer: FrameLayout
    private lateinit var spotifyLogo: ImageView
    private var particles = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashContainer = findViewById(R.id.splashContainer)
        spotifyLogo = findViewById(R.id.spotifyLogo)

        // Create particles
        createParticles()

        // Animate particles
        animateParticles()

        // Transition to main activity after 3.5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 3500)
    }

    private fun createParticles() {
        for (i in 0..19) {
            val particle = View(this).apply {
                setBackgroundColor(0xFF1DB954.toInt()) // Spotify green
                alpha = 0.7f
            }
            val size = (20 + Math.random() * 30).toInt()
            val params = FrameLayout.LayoutParams(size, size)
            params.leftMargin = (Math.random() * splashContainer.width).toInt()
            params.topMargin = (Math.random() * splashContainer.height).toInt()
            splashContainer.addView(particle, params)
            particles.add(particle)
        }
    }

    private fun animateParticles() {
        particles.forEachIndexed { index, particle ->
            val delay = (index * 50).toLong()
            val duration = (1500 + Math.random() * 1500).toLong()

            Handler(Looper.getMainLooper()).postDelayed({
                val animatorSet = AnimatorSet()

                // Fade out
                val fadeOut = ObjectAnimator.ofFloat(particle, "alpha", 0.7f, 0f).apply {
                    this.duration = duration
                }

                // Scale
                val scaleX = ObjectAnimator.ofFloat(particle, "scaleX", 1f, 0f).apply {
                    this.duration = duration
                }
                val scaleY = ObjectAnimator.ofFloat(particle, "scaleY", 1f, 0f).apply {
                    this.duration = duration
                }

                // Translate
                val translateX = ObjectAnimator.ofFloat(
                    particle,
                    "translationX",
                    0f,
                    (Math.random() * 600 - 300).toFloat()
                ).apply {
                    this.duration = duration
                }
                val translateY = ObjectAnimator.ofFloat(
                    particle,
                    "translationY",
                    0f,
                    (Math.random() * 600 - 300).toFloat()
                ).apply {
                    this.duration = duration
                }

                animatorSet.apply {
                    playTogether(fadeOut, scaleX, scaleY, translateX, translateY)
                    start()
                }
            }, delay)
        }

        // Logo animation
        Handler(Looper.getMainLooper()).postDelayed({
            val scaleIn = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(spotifyLogo, "scaleX", 0f, 1f).apply { duration = 800 },
                    ObjectAnimator.ofFloat(spotifyLogo, "scaleY", 0f, 1f).apply { duration = 800 },
                    ObjectAnimator.ofFloat(spotifyLogo, "alpha", 0f, 1f).apply { duration = 800 }
                )
                start()
            }
        }, 500)
    }
}