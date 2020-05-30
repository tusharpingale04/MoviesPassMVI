package com.tushar.movieappmvi.ui.main.movies.viewmodel

import androidx.lifecycle.LiveData
import com.tushar.movieappmvi.models.Keyword
import com.tushar.movieappmvi.models.Movie
import com.tushar.movieappmvi.models.SimilarMovie
import com.tushar.movieappmvi.repository.main.MoviesRepository
import com.tushar.movieappmvi.ui.BaseViewModel
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.main.movies.state.MovieStateEvent
import com.tushar.movieappmvi.ui.main.movies.state.MovieViewState
import com.tushar.movieappmvi.util.AbsentLiveData
import javax.inject.Inject

class MoviesViewModel
@Inject
constructor(
    private val moviesRepository: MoviesRepository
) : BaseViewModel<MovieStateEvent, MovieViewState>() {

    override fun getViewState() = MovieViewState()

    override fun handleStateEvent(stateEvent: MovieStateEvent): LiveData<DataState<MovieViewState>> {
        when (stateEvent) {
            is MovieStateEvent.FetchMovies -> {
                return moviesRepository.fetchMovies(
                    getSearchQuery(),
                    getPageNo()
                )
            }
            is MovieStateEvent.FetchKeywords -> {
                return moviesRepository.fetchMovieKeywords(stateEvent.id)
            }
            is MovieStateEvent.FetchSimilarMovies -> {
                return moviesRepository.fetchSimilarMovies(stateEvent.id,stateEvent.pageNo)
            }
            is MovieStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun cancelActiveJobs() {
        setStateEvent(MovieStateEvent.None)
        moviesRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}