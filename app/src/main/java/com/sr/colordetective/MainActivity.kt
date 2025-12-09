package com.sr.colordetective

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    
    private lateinit var cameraPreview: PreviewView
    private lateinit var colorIndicator: View
    private lateinit var missionCard: MaterialCardView
    private lateinit var missionText: TextView
    private lateinit var successText: TextView
    private lateinit var stars: Array<ImageView>
    
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var imageAnalyzer: ImageAnalysis? = null
    
    private val colors = listOf("red", "blue", "green", "yellow", "orange", "purple", "pink", "brown")
    private var currentTargetColor: String = "red"
    private var starsEarned = 0
    private var lastSuccessTime = 0L
    private val successCooldown = 2000L // 2 seconds between successes
    
    private var mediaPlayer: MediaPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupStars()
        requestCameraPermission()
    }
    
    private fun initializeViews() {
        cameraPreview = findViewById(R.id.cameraPreview)
        colorIndicator = findViewById(R.id.colorIndicator)
        missionCard = findViewById(R.id.missionCard)
        missionText = findViewById(R.id.missionText)
        successText = findViewById(R.id.successText)
        
        stars = arrayOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )
    }
    
    private fun setupStars() {
        stars.forEach { it.setImageResource(android.R.drawable.star_big_off) }
    }
    
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCamera()
            startNewMission()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
                startNewMission()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
            }
            
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ColorAnalyzer { colorName ->
                        runOnUiThread {
                            updateColorIndicator(colorName)
                            checkColorMatch(colorName)
                        }
                    })
                }
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun updateColorIndicator(colorName: String?) {
        if (colorName != null) {
            val colorRes = when (colorName.lowercase()) {
                "red" -> R.color.red
                "blue" -> R.color.blue
                "green" -> R.color.green
                "yellow" -> R.color.yellow
                "orange" -> R.color.orange
                "purple" -> R.color.purple
                "pink" -> R.color.pink
                "brown" -> R.color.brown
                else -> R.color.white
            }
            colorIndicator.setBackgroundColor(ContextCompat.getColor(this, colorRes))
        }
    }
    
    private fun checkColorMatch(detectedColor: String?) {
        val currentTime = System.currentTimeMillis()
        if (detectedColor != null && 
            detectedColor.lowercase() == currentTargetColor.lowercase() &&
            currentTime - lastSuccessTime > successCooldown) {
            
            lastSuccessTime = currentTime
            onColorFound()
        }
    }
    
    private fun onColorFound() {
        // Play success sound
        playSuccessSound()
        
        // Show success animation
        showSuccessAnimation()
        
        // Award star
        awardStar()
        
        // Start new mission after a delay
        missionText.postDelayed({
            startNewMission()
        }, 3000)
    }
    
    private fun playSuccessSound() {
        try {
            mediaPlayer?.release()
            // Try to use a system sound effect
            val soundPool = android.media.SoundPool.Builder().build()
            val soundId = soundPool.load(this, android.R.raw.sound_effects_000, 1)
            soundPool.setOnLoadCompleteListener { _, _, _ ->
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
            }
            // Release after a delay
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                soundPool.release()
            }, 2000)
        } catch (e: Exception) {
            // Fallback: use MediaPlayer with tone generator
            try {
                val toneGenerator = android.media.ToneGenerator(
                    android.media.AudioManager.STREAM_MUSIC,
                    100
                )
                toneGenerator.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 200)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
    
    private fun showSuccessAnimation() {
        successText.text = getString(R.string.found_color, currentTargetColor)
        successText.visibility = View.VISIBLE
        
        val bounceAnimation = AnimationUtils.loadAnimation(this, android.R.anim.bounce_interpolator)
        val scaleAnimation = AnimationUtils.loadAnimation(this, android.R.anim.anticipate_overshoot_interpolator)
        successText.startAnimation(scaleAnimation)
        
        successText.postDelayed({
            successText.visibility = View.GONE
        }, 2000)
    }
    
    private fun awardStar() {
        if (starsEarned < stars.size) {
            stars[starsEarned].setImageResource(android.R.drawable.star_big_on)
            val starAnimation = AnimationUtils.loadAnimation(this, android.R.anim.bounce_interpolator)
            stars[starsEarned].startAnimation(starAnimation)
            starsEarned++
        } else {
            // Reset stars when all earned
            starsEarned = 0
            stars.forEach { it.setImageResource(android.R.drawable.star_big_off) }
            Toast.makeText(this, "All stars earned! Starting over!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startNewMission() {
        currentTargetColor = colors.random()
        missionText.text = getString(R.string.find_color, currentTargetColor)
        
        // Animate mission card
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        missionCard.startAnimation(fadeIn)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        mediaPlayer?.release()
    }
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}

