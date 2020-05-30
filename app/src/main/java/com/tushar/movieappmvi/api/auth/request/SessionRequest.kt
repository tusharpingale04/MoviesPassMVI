package com.tushar.movieappmvi.api.auth.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SessionRequest(

    @SerializedName("request_token")
    @Expose
    var requestToken: String

) {
}