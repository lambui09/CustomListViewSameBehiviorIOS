package com.team_koder.myapplication

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var tempUri: Uri? = null
    private val REQUEST_PERMISSION = 1
    //todo: tao de a callback uri ve man hinh khac
    var onResultImage: ((uri: Uri?) -> Unit)? = {}

    private val pickImageAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data
            //todo a nhanh uri o day
            onResultImage?.invoke(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.btnShow)
        button.setOnClickListener {
            displayImages()
        }

    }

    private fun displayImages() {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val columnIndexName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(columnIndexId)
                val imageName = cursor.getString(columnIndexName)
                val imageData = cursor.getString(columnIndexData)

                // Use the image data as needed (e.g., load it into an ImageView)
                // For example, you can load it into an ImageView using a library like Glide:
                // Glide.with(this).load(imageData).into(imageView)

                // Here, we just print the image name and data to the console
                println("Image Name: $imageName")
                println("Image Data: $imageData")
                Log.d("@@@@@@", "Image Name: $imageName")
            }
        }
    }

    private fun createTemp(): Uri {
        val fileName = getFileName()
        val file = File(getOutputDirectory(this), fileName)
        return FileProvider.getUriForFile(
            this,

            "${this.packageName}.provider", // (use your app signature + ".provider" )
            file
        )
    }

    private fun getOutputDirectory(context: Context): File {
        val mediaDir = context.externalCacheDirs.firstOrNull()?.let {
            File(
                it,
                context.resources.getString(R.string.text_app_name_file_provider)
            ).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }

    private fun getFileName(): String {
        return SimpleDateFormat(
            FILE_NAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis()) + FILE_NAME_EXTENSION
    }

    companion object {
        const val FILE_NAME_FORMAT = "yyyyMMdd_HHmmss"
        const val FILE_NAME_EXTENSION = ".jpg"
    }
}