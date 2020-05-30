package com.tushar.movieappmvi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "auth_table",
    foreignKeys = [
        ForeignKey(
            entity = AccountProperties::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class AuthToken (

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "account_id")
    val id: Int? = -1,

    @SerializedName("session_id")
    @Expose
    @ColumnInfo(name = "session_id")
    val token: String? = null

)