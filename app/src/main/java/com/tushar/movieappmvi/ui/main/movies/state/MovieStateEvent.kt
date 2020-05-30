package com.tushar.movieappmvi.ui.main.movies.state

sealed class MovieStateEvent {
    object FetchMovies : MovieStateEvent()
    data class FetchKeywords(val id: String) : MovieStateEvent()
    data class FetchSimilarMovies(val id: String, val pageNo: String) : MovieStateEvent()
    object None: MovieStateEvent()
}