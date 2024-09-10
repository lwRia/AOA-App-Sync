package com.app.appsync.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

class DownloadImageTask(private val bmImage: ImageView) {

    fun execute(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = downloadImage(url)
            withContext(Dispatchers.Main) {
                bmImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun downloadImage(url: String): Bitmap? {
        return try {
            val inputStream: InputStream = URL(url).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
