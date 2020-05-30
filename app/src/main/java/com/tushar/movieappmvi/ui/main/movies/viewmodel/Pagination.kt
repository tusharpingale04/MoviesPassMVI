package com.tushar.movieappmvi.ui.main.movies.viewmodel

import android.util.Log
import com.tushar.movieappmvi.ui.main.movies.BaseMoviesFragment.Companion.TAG
import com.tushar.movieappmvi.ui.main.movies.state.MovieStateEvent
import com.tushar.movieappmvi.ui.main.movies.state.MovieViewState

fun MoviesViewModel.resetPage() {
    val update = getCurrentViewStateOrNew()
    update.movieListFields.page = 1
    setViewState(update)
}

fun MoviesViewModel.loadFirstPage(){
    setIsQueryInProgress(true)
    setIsQueryExhausted(false)
    resetPage()
    setStateEvent(MovieStateEvent.FetchMovies)
}

fun MoviesViewModel.incrementPageNo(){
    val update = getCurrentViewStateOrNew()
    val page = update.copy().movieListFields.page
    update.movieListFields.page = page+1
    setViewState(update)
}

fun MoviesViewModel.loadNextPage(){
    if(!isSearchQueryExhausted() || !isSearchQueryInProgress()){
        incrementPageNo()
        setIsQueryInProgress(true)
        setStateEvent(MovieStateEvent.FetchMovies)
    }
}

fun MoviesViewModel.handleMoviesList(viewState: MovieViewState){
    Log.d(TAG, "MoviesViewModel, DataState: $viewState")
    Log.d(TAG, "MoviesViewModel, DataState: isQueryInProgress?: " +
            "${viewState.movieListFields.isQueryInProgress}")
    Log.d(TAG, "MoviesViewModel, DataState: isQueryExhausted?: " +
            "${viewState.movieListFields.isQueryExhausted}")
    setIsQueryInProgress(viewState.movieListFields.isQueryInProgress)
    setIsQueryExhausted(viewState.movieListFields.isQueryExhausted)
    setMovieList(viewState.movieListFields.movieList)
}





