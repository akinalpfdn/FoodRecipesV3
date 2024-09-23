package com.example.foodrecipesv3.utils


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
object ImageUtils {

    fun resizeImage(context: Context, imageUri: Uri, targetWidth: Int = 1024, quality: Int = 85): Uri? {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetWidth * bitmap.height / bitmap.width, true)

        // Save resized bitmap to cache directory and return new URI
        val cacheDir = context.cacheDir
        val file = File(cacheDir, "resized_image_${System.currentTimeMillis()}.jpg")

        return try {
            val outputStream = FileOutputStream(file)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream) // Reduce quality
            outputStream.flush()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}