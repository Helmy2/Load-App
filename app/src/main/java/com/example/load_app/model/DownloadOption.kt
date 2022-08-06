package com.example.load_app.model


sealed class DownloadOption() {
    object Glide : DownloadOption()
    object LoadApp : DownloadOption()
    object Retrofit : DownloadOption()
}
