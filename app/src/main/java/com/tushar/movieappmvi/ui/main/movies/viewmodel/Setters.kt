package com.tushar.movieappmvi.ui.main.movies.viewmodel

import com.tushar.movieappmvi.models.Keyword
import com.tushar.movieappmvi.models.Movie
import com.tushar.movieappmvi.models.SimilarMovie

fun MoviesViewModel.setMovieList(moviesList: List<Movie>) {
    val update = getCurrentViewStateOrNew()
    update.movieListFields.movieList = moviesList
    setViewState(update)
}

fun MoviesViewModel.setQuery(query: String) {
    val update = getCurrentViewStateOrNew()
    update.movieListFields.searchQuery = query
    setViewState(update)
}

fun MoviesViewModel.setMovie(movie: Movie) {
    val update = getCurrentViewStateOrNew()
    update.movieFields.movieDetail = movie
    setViewState(update)
}

fun MoviesViewModel.setKeywords(keywordList: List<Keyword>) {
    val update = getCurrentViewStateOrNew()
    if (keywordList == update.movieFields.keywordsList) {
        return
    }
    update.movieFields.keywordsList = keywordList
    setViewState(update)
}

fun MoviesViewModel.setSimilarMovies(similarMovies: List<SimilarMovie>) {
    val update = getCurrentViewStateOrNew()
    if (similarMovies == update.movieFields.similarMoviesList) {
        return
    }
    update.movieFields.similarMoviesList = similarMovies
    setViewState(update)
}

fun MoviesViewModel.setIsQueryExhausted(queryExhausted: Boolean){
    val update = getCurrentViewStateOrNew()
    update.movieListFields.isQueryExhausted = queryExhausted
    setViewState(update)
}

fun MoviesViewModel.setIsQueryInProgress(queryInProgress: Boolean){
    val update = getCurrentViewStateOrNew()
    update.movieListFields.isQueryInProgress = queryInProgress
    setViewState(update)
}