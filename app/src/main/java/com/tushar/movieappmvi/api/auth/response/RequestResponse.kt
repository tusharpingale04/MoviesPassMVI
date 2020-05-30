package com.tushar.movieappmvi.api.auth.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RequestResponse(
    @SerializedName("request_token")
    @Expose
    var requestToken: String ?= ""
)