package com.tushar.movieappmvi.di.auth

import android.content.SharedPreferences
import com.tushar.movieappmvi.api.auth.TMDBAuthService
import com.tushar.movieappmvi.db.AccountPropertiesDao
import com.tushar.movieappmvi.db.AuthTokenDao
import com.tushar.movieappmvi.repository.auth.AuthRepository
import com.tushar.movieappmvi.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    @AuthScope
    @Provides
    fun provideAuthApiService(retrofitBuilder: Retrofit.Builder) : TMDBAuthService{
        return retrofitBuilder
            .build()
            .create(TMDBAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        accountPropertiesDao: AccountPropertiesDao,
        authTokenDao: AuthTokenDao,
        tmdbAuthService: TMDBAuthService,
        sharedPreferences: SharedPreferences,
        sharedPrefsEditor: SharedPreferences.Editor
    ) : AuthRepository{
        return AuthRepository(authTokenDao,accountPropertiesDao,tmdbAuthService,sessionManager,sharedPreferences,sharedPrefsEditor)
    }
}