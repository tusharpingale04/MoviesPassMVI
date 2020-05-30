package com.tushar.movieappmvi.models


import androidx.room.*

@Entity(tableName = "keyword_table")
data class KeywordModel(

    @PrimaryKey(autoGenerate = false)
    @ForeignKey(entity = Movie::class,
        parentColumns = ["id"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE)
    @ColumnInfo(name = "id")
    var id: Int = -1,

    @ColumnInfo(name = "keywords")
    var keywords: List<Keyword>? = null

)