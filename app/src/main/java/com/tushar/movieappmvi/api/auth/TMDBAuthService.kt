package com.tushar.movieappmvi.api.auth

import androidx.lifecycle.LiveData
import com.tushar.movieappmvi.api.auth.request.LoginRequest
import com.tushar.movieappmvi.api.auth.request.SessionRequest
import com.tushar.movieappmvi.api.auth.response.LoginResponse
import com.tushar.movieappmvi.api.auth.response.RequestResponse
import com.tushar.movieappmvi.api.auth.response.SessionResponse
import com.tushar.movieappmvi.util.GenericApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TMDBAuthService {

    @GET("authentication/token/new")
    fun getRequestToken() : LiveData<GenericApiResponse<RequestResponse>>

    @POST("authentication/token/validate_with_login")
    fun login(@Body request: LoginRequest) : LiveData<GenericApiResponse<LoginResponse>>

    @POST("authentication/session/new")
    fun createSession(@Body requestToken: SessionRequest) : LiveData<GenericApiResponse<SessionResponse>>

}