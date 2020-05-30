package com.tushar.movieappmvi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.tushar.movieappmvi.api.main.MainApiService
import com.tushar.movieappmvi.api.main.response.KeywordsResponse
import com.tushar.movieappmvi.api.main.response.MovieDetails
import com.tushar.movieappmvi.db.MovieDao
import com.tushar.movieappmvi.models.*
import com.tushar.movieappmvi.repository.JobManager
import com.tushar.movieappmvi.repository.NetworkBoundResource
import com.tushar.movieappmvi.session.SessionManager
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.main.movies.state.MovieViewState
import com.tushar.movieappmvi.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.tushar.movieappmvi.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MoviesRepository
@Inject
constructor(
    private val mainApiService: MainApiService,
    private val movieDao: MovieDao,
    private val sessionManager: SessionManager
) : JobManager(TAG) {

    fun fetchMovies(query: String, pageNo: Int): LiveData<DataState<MovieViewState>> {
        return object : NetworkBoundResource<MovieDetails, List<Movie>, MovieViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<MovieDetails>) {

                val moviesList: ArrayList<Movie> = ArrayList()
                response.data.results?.let { list ->
                    for (movie in list) {
                        moviesList.add(
                            Movie(
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
                return if (query.isEmpty()) {
                    mainApiService.getMovies(pageNo.toString())
                } else {
                    mainApiService.searchMovies(query,pageNo.toString())
                }
            }

            override fun loadFromCache(): LiveData<MovieViewState> {
                return movieDao.getMovies(query = query,pageNo = pageNo)
                    .switchMap {
                        object : LiveData<MovieViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = MovieViewState(
                                    movieListFields = MovieViewState.MovieListFields(
                                        movieList = it
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cachedObject: List<Movie>?) {
                cachedObject?.let { moviesList ->
                    withContext(Dispatchers.IO) {
                        try {
                            for (movie in moviesList) {
                                launch {
                                    movieDao.insertMovie(movie)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "updateLocalDb: Failed to insert in movies table")

                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("fetchMovies", job)
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) { viewState ->
                        viewState.movieListFields.isQueryInProgress = false
                        if(pageNo * PAGINATION_PAGE_SIZE > viewState.movieListFields.movieList.size){
                            viewState.movieListFields.isQueryExhausted = true
                        }
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

    fun fetchMovieKeywords(id: String): LiveData<DataState<MovieViewState>> {
        return object : NetworkBoundResource<KeywordsResponse, KeywordModel, MovieViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<KeywordsResponse>) {
                val keywordsList: ArrayList<Keyword> = ArrayList()
                response.data.let { keywordsModel ->
                    if (keywordsModel.id != null && !keywordsModel.keywords.isNullOrEmpty()) {
                        for (keyword in keywordsModel.keywords!!) {
                            keywordsList.add(
                                Keyword(
                                    id = keyword.id,
                                    name = keyword.name
                                )
                            )
                        }
                    }
                }
                updateLocalDb(KeywordModel(id = response.data.id!!, keywords = keywordsList))
                createCacheRequestAndReturn()
            }

            override suspend fun createCall(): LiveData<GenericApiResponse<KeywordsResponse>> {
                return mainApiService.getKeywords(id)
            }

            override fun loadFromCache(): LiveData<MovieViewState> {
                return movieDao.getKeyword(id.toInt()).switchMap {
                    object : LiveData<MovieViewState>() {
                        override fun onActive() {
                            super.onActive()
                            value = MovieViewState(
                                movieFields = MovieViewState.MovieFields(
                                    keywordsList = it?.keywords
                                )
                            )
                        }
                    }
                }
            }

            override suspend fun updateLocalDb(cachedObject: KeywordModel?) {
                cachedObject?.let { keywordModel ->
                    withContext(Dispatchers.IO) {
                        movieDao.insertKeywords(keywordModel)
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("fetchMovieKeywords", job = job, cancelJob = false)
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) {
                        onCompleteJob(
                            DataState.data(
                                data = it,
                                response = null
                            )
                        )
                    }
                }
            }

        }.asLiveData()
    }

    fun fetchSimilarMovies(id: String, pageNo: String): LiveData<DataState<MovieViewState>> {
        return object : NetworkBoundResource<MovieDetails, SimilarMoviesModel, MovieViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<MovieDetails>) {
                val similarMoviesList: ArrayList<SimilarMovie> = ArrayList()
                response.data.let { movies ->
                    if (!movies.results.isNullOrEmpty()) {
                        for (movie in movies.results!!) {
                            similarMoviesList.add(
                                SimilarMovie(
                                    id = movie.id,
                                    originalTitle = movie.originalTitle,
                                    voteAverage = movie.voteAverage,
                                    posterPath = movie.posterPath ?: ""
                                )
                            )
                        }
                    }
                }
                updateLocalDb(SimilarMoviesModel(id = id.toInt(), similarMovies = similarMoviesList))
                createCacheRequestAndReturn()
            }

            override suspend fun createCall(): LiveData<GenericApiResponse<MovieDetails>> {
                return mainApiService.getSimilarMovies(id,pageNo)
            }

            override fun loadFromCache(): LiveData<MovieViewState> {
                return movieDao.getSimilarMovies(id.toInt()).switchMap {
                    object : LiveData<MovieViewState>() {
                        override fun onActive() {
                            super.onActive()
                            value = MovieViewState(
                                movieFields = MovieViewState.MovieFields(
                                    similarMoviesList = it?.similarMovies
                                )
                            )
                        }
                    }
                }
            }

            override suspend fun updateLocalDb(cachedObject: SimilarMoviesModel?) {
                cachedObject?.let { movies ->
                    withContext(Dispatchers.IO) {
                        movieDao.insertSimilarMovies(movies)
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("fetchSimilarMovies", job = job, cancelJob = false)
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) {
                        onCompleteJob(
                            DataState.data(
                                data = it,
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