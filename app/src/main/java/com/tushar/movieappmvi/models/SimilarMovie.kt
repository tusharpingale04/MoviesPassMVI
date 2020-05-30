package com.tushar.movieappmvi.models

import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SimilarMovie(

    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    var id: Int,

    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    @Expose
    var originalTitle: String,

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    @Expose
    var voteAverage: Double,

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = ""
)