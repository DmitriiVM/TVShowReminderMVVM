package com.example.tvshowreminder.util

import android.os.Build
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tvshowreminder.R
import com.example.tvshowreminder.util.ErrorImageOrientation.*
import java.text.SimpleDateFormat
import java.util.*

enum class ErrorImageOrientation{
    HORIZONTAL, VERTICAL
}

internal fun ImageView.setImage(posterPath: String, orientation: ErrorImageOrientation){


    val requestOptions = RequestOptions()
    when (orientation){
        HORIZONTAL -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                requestOptions.error(R.drawable.no_image_available_horizontal)
            } else {
                requestOptions.error(R.drawable.ic_sentiment_dissatisfied)
            }
        }
        VERTICAL -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                requestOptions.error(R.drawable.no_image_available_vertical)
            } else {
                requestOptions.error(R.drawable.ic_sentiment_dissatisfied)
            }
        }
    }

        Glide
            .with(context)
            .setDefaultRequestOptions(requestOptions)
            .load(BASE_IMAGE_URL + posterPath)
            .into(this)
}

internal fun getDeviceLanguage(): String {
    return Locale.getDefault().toLanguageTag()
}

internal fun getCurrentDate(): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return simpleDateFormat.format(Date())
}

internal fun String.convertStringToDate(): Date? {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return simpleDateFormat.parse(this)
}