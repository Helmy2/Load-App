package com.example.load_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.load_app.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }
}
