package com.tushar.movieappmvi.api.auth.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    @Expose
    var success: Boolean? = false,
    @SerializedName("request_token")
    @Expose
    var requestToken: String? = ""
)