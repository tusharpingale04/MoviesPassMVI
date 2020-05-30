package com.tushar.movieappmvi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "similar_movies_table")
data class SimilarMoviesModel(

    @PrimaryKey(autoGenerate = false)
    @ForeignKey(entity = Movie::class,
        parentColumns = ["id"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE)
    @ColumnInfo(name = "id")
    var id: Int = -1,

    @ColumnInfo(name = "similar_movies")
    var similarMovies: List<SimilarMovie>? = null

)