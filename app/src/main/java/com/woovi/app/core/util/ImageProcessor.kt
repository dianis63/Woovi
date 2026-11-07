package com.woovi.app.core.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class InvalidImageException(message: String) : Exception(message)

object ImageProcessor {
    private const val MAX_SIDE = 1200
    private const val MAX_INPUT_BYTES = 25L * 1024 * 1024
    private const val MAX_OUTPUT_BYTES = 2 * 1024 * 1024
    private val ALLOWED_TYPES = setOf("image/jpeg", "image/png", "image/webp")

    suspend fun toJpegBytes(context: Context, uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver

        val mime = resolver.getType(uri)
        if (mime != null && mime !in ALLOWED_TYPES) {
            throw InvalidImageException("Elige una imagen JPG, PNG o WebP.")
        }

        val originalSize = resolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: -1L
        if (originalSize > MAX_INPUT_BYTES) {
            throw InvalidImageException("Esa imagen es demasiado grande. Elige una de menos de 25 MB.")
        }

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw InvalidImageException("No pudimos leer esa imagen. Prueba con otra.")
        }

        var sample = 1
        while (bounds.outWidth / (sample * 2) >= MAX_SIDE && bounds.outHeight / (sample * 2) >= MAX_SIDE) {
            sample *= 2
        }
        val decoded = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: throw InvalidImageException("No pudimos leer esa imagen. Prueba con otra.")

        val degrees = readRotationDegrees(resolver, uri)
        val rotated = if (degrees != 0) {
            val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
            Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
        } else decoded

        val scale = min(1f, MAX_SIDE.toFloat() / max(rotated.width, rotated.height))
        val w = (rotated.width * scale).roundToInt().coerceAtLeast(1)
        val h = (rotated.height * scale).roundToInt().coerceAtLeast(1)
        val flat = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(flat)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(rotated, null, Rect(0, 0, w, h), Paint(Paint.FILTER_BITMAP_FLAG))

        var quality = 85
        var bytes: ByteArray
        do {
            val out = ByteArrayOutputStream()
            flat.compress(Bitmap.CompressFormat.JPEG, quality, out)
            bytes = out.toByteArray()
            quality -= 15
        } while (bytes.size > MAX_OUTPUT_BYTES && quality >= 40)

        if (bytes.size > MAX_OUTPUT_BYTES) {
            throw InvalidImageException("La imagen sigue siendo muy pesada. Prueba con otra.")
        }
        bytes
    }

    private fun readRotationDegrees(resolver: ContentResolver, uri: Uri): Int = try {
        resolver.openInputStream(uri)?.use { stream ->
            when (ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } ?: 0
    } catch (e: Exception) {
        0
    }
}