package com.tushar.movieappmvi.api

import com.tushar.movieappmvi.BuildConfig
import com.tushar.movieappmvi.session.SessionManager
import com.tushar.movieappmvi.util.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor /*@Inject constructor(val sessionManager: SessionManager)*/: Interceptor {

    companion object{
        const val API_KEY = "api_key"
        const val SESSION_ID = "session_id"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain
            .request()
            .url()
            .newBuilder()
            .addQueryParameter(API_KEY, BuildConfig.API_KEY)
            .build()
        /*val authToken = sessionManager.cachedToken.value
        if(authToken?.token != null){
            url.addQueryParameter(SESSION_ID,authToken.token)
        }*/
        return chain.proceed(chain.request().newBuilder().url(url).build())
    }
}