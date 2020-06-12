package com.tushar.movieappmvi.api.main

import androidx.lifecycle.LiveData
import com.tushar.movieappmvi.api.ApiKeyInterceptor
import com.tushar.movieappmvi.api.main.response.KeywordsResponse
import com.tushar.movieappmvi.api.main.response.MovieDetails
import com.tushar.movieappmvi.models.AccountProperties
import com.tushar.movieappmvi.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MainApiService {

    @GET("account")
    fun getAccountDetails(
        @Query(ApiKeyInterceptor.SESSION_ID) sessionId: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @GET("movie/popular")
    fun getMovies(
        @Query("page") page: String
    ) : LiveData<GenericApiResponse<MovieDetails>>

    @GET("search/movie")
    fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: String
    ) : LiveData<GenericApiResponse<MovieDetails>>

    @GET("movie/{movieId}/keywords")
    fun getKeywords(
        @Path("movieId") id: String
    ) : LiveData<GenericApiResponse<KeywordsResponse>>

    @GET("movie/{movieId}/similar")
    fun getSimilarMovies(
        @Path("movieId") id: String,@Query("page") page: String
    ): LiveData<GenericApiResponse<MovieDetails>>

    @GET("account/account_id/favorite/movies")
    fun getFavorites(
        @Query(ApiKeyInterceptor.SESSION_ID) sessionId: String
    ): LiveData<GenericApiResponse<MovieDetails>>
}