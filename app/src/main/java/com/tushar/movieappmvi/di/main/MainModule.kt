package com.tushar.movieappmvi.di.main

import com.tushar.movieappmvi.api.main.MainApiService
import com.tushar.movieappmvi.db.AccountPropertiesDao
import com.tushar.movieappmvi.db.AppDatabase
import com.tushar.movieappmvi.db.FavoritesDao
import com.tushar.movieappmvi.db.MovieDao
import com.tushar.movieappmvi.repository.main.AccountRepository
import com.tushar.movieappmvi.repository.main.FavoritesRepository
import com.tushar.movieappmvi.repository.main.MoviesRepository
import com.tushar.movieappmvi.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule{
    @MainScope
    @Provides
    fun provideMainApiService(retrofitBuilder: Retrofit.Builder) : MainApiService {
        return retrofitBuilder
            .build()
            .create(MainApiService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        sessionManager: SessionManager,
        accountPropertiesDao: AccountPropertiesDao,
        mainApiService: MainApiService
    ) : AccountRepository{
        return AccountRepository(sessionManager,accountPropertiesDao,mainApiService)
    }

    @MainScope
    @Provides
    fun provideMovieDao(appDatabase: AppDatabase) : MovieDao{
        return appDatabase.getMovie()
    }

    @MainScope
    @Provides
    fun provideMoviesRepository(
        sessionManager: SessionManager,
        movieDao: MovieDao,
        mainApiService: MainApiService
    ) : MoviesRepository{
        return MoviesRepository(mainApiService,movieDao,sessionManager)
    }

    @MainScope
    @Provides
    fun provideFavorites(appDatabase: AppDatabase): FavoritesDao{
        return appDatabase.getFavoriteMovie()
    }

    @MainScope
    @Provides
    fun provideFavoritesRepository(
        sessionManager: SessionManager,
        favoriteDao: FavoritesDao,
        mainApiService: MainApiService
    ) : FavoritesRepository{
        return FavoritesRepository(sessionManager,favoriteDao,mainApiService)
    }


}