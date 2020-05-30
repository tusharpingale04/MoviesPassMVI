package com.tushar.movieappmvi.api.main.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
@Keep
data class Keyword(
    @SerializedName("id")
    @Expose
    var id: Int? = 0,
    @SerializedName("name")
    @Expose
    var name: String? = ""
) : Parcelable