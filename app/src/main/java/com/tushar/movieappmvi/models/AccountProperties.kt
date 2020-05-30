package com.tushar.movieappmvi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "account_properties")
data class AccountProperties(

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    val name: String,

    @SerializedName("username")
    @Expose
    @ColumnInfo(name = "username")
    val username: String
)