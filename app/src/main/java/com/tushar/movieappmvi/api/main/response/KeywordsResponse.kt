package com.tushar.movieappmvi.api.main.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
@Keep
data class KeywordsResponse(
    @SerializedName("id")
    @Expose
    var id: Int? = -1,
    @SerializedName("keywords")
    @Expose
    var keywords: List<Keyword>? = listOf()
) : Parcelable