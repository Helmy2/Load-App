package com.example.load_app

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.load_app.databinding.ActivityMainBinding
import com.example.load_app.model.DownloadDetails
import com.example.load_app.model.DownloadOption
import com.example.load_app.model.DownloadStatus
import java.io.File

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )

        binding.customButton.setOnClickListener {
            val downloadOption = getDownloadOption()
            if (downloadOption == null)
                Toast.makeText(
                    this,
                    getString(R.string.not_select_item_massage),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                binding.customButton.startAnimation()
                try {
                    download(getUrl(downloadOption), getDescription(downloadOption))
                } catch (e: Exception) {
                    Log.d(TAG, "onCreate: ${e.message}")
                }
            }
        }
    }

    private fun getDownloadOption(): DownloadOption? {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.gildeRadioButton -> DownloadOption.Glide
            R.id.loadAppRadioButton -> DownloadOption.LoadApp
            R.id.retrofitRadioButton -> DownloadOption.Retrofit
            else -> null
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            var downloadDetails: DownloadDetails? = null
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val q = DownloadManager.Query()
                q.setFilterById(id)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val c: Cursor = downloadManager.query(q)
                if (c.moveToFirst()) {
                    binding.customButton.stopAnimation()
                    val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadDetails = DownloadDetails(title, DownloadStatus.SUCCESS)
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloadDetails = DownloadDetails(title, DownloadStatus.FAILED)
                    }
                    notificationManager.sendNotification(downloadDetails!!,applicationContext)
                }
                Log.i(TAG, "onReceive: ${downloadDetails?.name} ${downloadDetails?.status}")
            }
        }

    }

    private fun download(url: String, title: String) {


        val file = File(getExternalFilesDir(null), "/repos")

        if (!file.exists()) {
            file.mkdirs()
        }

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/repos/repository.zip"
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getUrl(downloadOption: DownloadOption): String {
        return when (downloadOption) {
            DownloadOption.Glide -> resources.getString(R.string.glide_url)
            DownloadOption.LoadApp -> resources.getString(R.string.load_app_url)
            DownloadOption.Retrofit -> resources.getString(R.string.retrofit_url)
        }
    }

    private fun getDescription(downloadOption: DownloadOption): String {
        return when (downloadOption) {
            DownloadOption.Glide -> resources.getString(R.string.glide)
            DownloadOption.LoadApp -> resources.getString(R.string.load_app)
            DownloadOption.Retrofit -> resources.getString(R.string.retrofit)
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_is_done)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}



