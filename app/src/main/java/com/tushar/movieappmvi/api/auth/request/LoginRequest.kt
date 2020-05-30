package com.tushar.movieappmvi.api.auth.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @SerializedName("username")
    @Expose
    var username: String,

    @SerializedName("password")
    @Expose
    var password: String,

    @SerializedName("request_token")
    @Expose
    var requestToken: String

) {
}