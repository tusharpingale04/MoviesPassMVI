package com.tushar.movieappmvi.api.main.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
@Keep
data class MovieDetails(
    @SerializedName("results")
    @Expose
    var results: List<Result>? = listOf()
) : Parcelable