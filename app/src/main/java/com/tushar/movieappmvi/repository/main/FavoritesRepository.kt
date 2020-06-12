package com.tushar.movieappmvi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.tushar.movieappmvi.api.main.MainApiService
import com.tushar.movieappmvi.api.main.response.MovieDetails
import com.tushar.movieappmvi.db.FavoritesDao
import com.tushar.movieappmvi.models.FavoriteMovie
import com.tushar.movieappmvi.repository.JobManager
import com.tushar.movieappmvi.repository.NetworkBoundResource
import com.tushar.movieappmvi.session.SessionManager
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.main.favorites.state.FavoritesViewState
import com.tushar.movieappmvi.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    val sessionManager: SessionManager,
    val favoritesDao: FavoritesDao,
    val apiService: MainApiService
) : JobManager(TAG){

    fun fetchFavoriteMovies(sessionId: String) : LiveData<DataState<FavoritesViewState>>{
        return object : NetworkBoundResource<MovieDetails, List<FavoriteMovie>,FavoritesViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ){
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<MovieDetails>) {
                val moviesList: ArrayList<FavoriteMovie> = ArrayList()
                response.data.results?.let { list ->
                    for (movie in list) {
                        moviesList.add(
                            FavoriteMovie(
                                id = movie.id,
                                originalTitle = movie.originalTitle,
                                overview = movie.overview,
                                voteAverage = movie.voteAverage,
                                releaseDate = movie.releaseDate,
                                posterPath = movie.posterPath ?: "",
                                backdropPath = movie.backdropPath ?: "",
                                popularity = movie.popularity,
                                voteCount = movie.voteCount,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    updateLocalDb(moviesList)
                    createCacheRequestAndReturn()
                }
            }

            override suspend fun createCall(): LiveData<GenericApiResponse<MovieDetails>> {
                return apiService.getFavorites(sessionId)
            }

            override fun loadFromCache(): LiveData<FavoritesViewState> {
                return favoritesDao.getFavoritesMovies().switchMap {
                    object : LiveData<FavoritesViewState>(){
                        override fun onActive() {
                            super.onActive()
                            value = FavoritesViewState(
                                movieList = it
                            )
                        }
                    }
                }
            }

            override suspend fun updateLocalDb(cachedObject: List<FavoriteMovie>?) {
                cachedObject?.let { movieList ->
                    withContext(Dispatchers.IO) {
                        try {
                            if(movieList.isEmpty()){
                                favoritesDao.insertMovie(null)
                            }
                            for (movie in movieList) {
                                launch {
                                    favoritesDao.insertMovie(movie)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(MoviesRepository.TAG, "updateLocalDb: Failed to insert in movies table")
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("fetchFavoritesMovies", job)
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main) {
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(
                            DataState.data(
                                data = viewState,
                                response = null
                            )
                        )
                    }
                }
            }

        }.asLiveData()
    }

    companion object {
        const val TAG = "MoviesRepository"
    }
}