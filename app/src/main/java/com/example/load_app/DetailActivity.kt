package com.example.load_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.load_app.databinding.ActivityDetailBinding
import com.example.load_app.model.DownloadDetails
import com.example.load_app.notification.NOTIFICATION_DETAILS_KEY

private const val TAG = "DetailActivity"

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.lifecycleOwner = this

        binding.motionLayout

        intent.getParcelableExtra<DownloadDetails?>(NOTIFICATION_DETAILS_KEY)?.also {
            binding.downloadDetails = it
        }

        binding.okButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
