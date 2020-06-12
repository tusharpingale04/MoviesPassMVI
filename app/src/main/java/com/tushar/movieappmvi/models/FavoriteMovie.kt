package com.tushar.movieappmvi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class FavoriteMovie(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "original_title")
    var originalTitle: String,

    @ColumnInfo(name = "overview")
    var overview: String,

    @ColumnInfo(name = "vote_average")
    var voteAverage: Double,

    @ColumnInfo(name = "release_date")
    var releaseDate: String,

    @ColumnInfo(name = "poster_path")
    var posterPath: String? = "",

    @ColumnInfo(name = "backdrop_path")
    var backdropPath: String? = "",

    @ColumnInfo(name = "popularity")
    var popularity: Double,

    @ColumnInfo(name = "vote_count")
    var voteCount: Long,

    @ColumnInfo(name = "timestamp_in_millis")
    var timestamp: Long
){
    override fun toString(): String {
        return "Movie(id=$id, originalTitle='$originalTitle', overview='$overview', voteAverage=$voteAverage, releaseDate='$releaseDate', posterPath='$posterPath', backdropPath='$backdropPath', popularity=$popularity, voteCount=$voteCount)"
    }
}