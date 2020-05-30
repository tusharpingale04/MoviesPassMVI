package com.tushar.movieappmvi.api.auth.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SessionResponse(
    @SerializedName("session_id")
    @Expose
    var sessionId: String ?= ""
)