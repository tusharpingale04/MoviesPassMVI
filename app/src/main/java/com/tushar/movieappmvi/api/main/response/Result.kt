package com.tushar.movieappmvi.api.main.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Result(

    @SerializedName("adult")
    @Expose
    var adult: Boolean,

    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = "",

    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("original_language")
    @Expose
    var originalLanguage: String,

    @SerializedName("original_title")
    @Expose
    var originalTitle: String,

    @SerializedName("overview")
    @Expose
    var overview: String,

    @SerializedName("popularity")
    @Expose
    var popularity: Double,

    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = "",

    @SerializedName("release_date")
    @Expose
    var releaseDate: String,

    @SerializedName("title")
    @Expose
    var title: String,

    @SerializedName("video")
    @Expose
    var video: Boolean,

    @SerializedName("vote_average")
    @Expose
    var voteAverage: Double,

    @SerializedName("vote_count")
    @Expose
    var voteCount: Long

) : Parcelable