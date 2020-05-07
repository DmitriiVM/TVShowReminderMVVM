package com.example.tvshowreminder.util

import kotlinx.android.parcel.RawValue

sealed class Resource<T> {

    class Loading<T>() : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    data class Success<T>(val data:@RawValue  T) : Resource<T>()
    data class SuccessWithMessage<T>(val data: T, val networkErrorMessage: String) : Resource<T>()
}
