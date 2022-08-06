package com.example.load_app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadDetails(
    val name: String,
    val status: DownloadStatus
): Parcelable

enum class DownloadStatus(val text: String){
    SUCCESS("Success"),
    FAILED("Failed")
}

