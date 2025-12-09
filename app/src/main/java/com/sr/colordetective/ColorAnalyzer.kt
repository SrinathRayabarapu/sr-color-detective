package com.sr.colordetective

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class ColorAnalyzer(private val onColorDetected: (String?) -> Unit) : ImageAnalysis.Analyzer {
    
    override fun analyze(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            val dominantColor = getDominantColor(bitmap)
            val colorName = identifyColor(dominantColor)
            
            onColorDetected(colorName)
        } catch (e: Exception) {
            e.printStackTrace()
            onColorDetected(null)
        } finally {
            imageProxy.close()
        }
    }
    
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        if (imageProxy.format != ImageFormat.YUV_420_888) {
            return null
        }
        
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer
        
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        
        val nv21 = ByteArray(ySize + uSize + vSize)
        
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 75, out)
        val imageBytes = out.toByteArray()
        
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
    
    private fun getDominantColor(bitmap: Bitmap?): Int {
        if (bitmap == null) return 0
        
        // Sample center region of the image for better accuracy
        val width = bitmap.width
        val height = bitmap.height
        val sampleSize = 10
        val startX = (width / 2) - (width / 4)
        val startY = (height / 2) - (height / 4)
        val endX = (width / 2) + (width / 4)
        val endY = (height / 2) + (height / 4)
        
        var r = 0
        var g = 0
        var b = 0
        var count = 0
        
        for (x in startX until endX step sampleSize) {
            for (y in startY until endY step sampleSize) {
                if (x < width && y < height) {
                    val pixel = bitmap.getPixel(x, y)
                    r += android.graphics.Color.red(pixel)
                    g += android.graphics.Color.green(pixel)
                    b += android.graphics.Color.blue(pixel)
                    count++
                }
            }
        }
        
        if (count == 0) return 0
        
        r /= count
        g /= count
        b /= count
        
        return android.graphics.Color.rgb(r, g, b)
    }
    
    private fun identifyColor(color: Int): String? {
        val r = android.graphics.Color.red(color)
        val g = android.graphics.Color.green(color)
        val b = android.graphics.Color.blue(color)
        
        // Calculate brightness
        val brightness = (r + g + b) / 3
        
        // If too dark or too light, return null
        if (brightness < 30 || brightness > 220) {
            return null
        }
        
        // Color identification logic
        return when {
            // Red
            r > g + 50 && r > b + 50 -> "red"
            // Blue
            b > r + 50 && b > g + 50 -> "blue"
            // Green
            g > r + 30 && g > b + 30 -> "green"
            // Yellow
            r > 180 && g > 180 && b < 100 -> "yellow"
            // Orange
            r > 200 && g > 100 && g < 200 && b < 100 -> "orange"
            // Purple
            r > 100 && r < 200 && g < 100 && b > 100 && b < 200 -> "purple"
            // Pink
            r > 200 && g > 150 && g < 200 && b > 150 && b < 200 -> "pink"
            // Brown
            r > 100 && r < 180 && g > 50 && g < 150 && b < 100 -> "brown"
            else -> null
        }
    }
}

