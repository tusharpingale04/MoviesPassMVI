package com.tushar.movieappmvi.ui.main.movies.viewmodel

fun MoviesViewModel.getPageNo() : Int{
    return getCurrentViewStateOrNew().movieListFields.page
}

fun MoviesViewModel.getSearchQuery() : String{
    return getCurrentViewStateOrNew().movieListFields.searchQuery
}

fun MoviesViewModel.isSearchQueryInProgress() : Boolean{
    return getCurrentViewStateOrNew().movieListFields.isQueryInProgress
}

fun MoviesViewModel.isSearchQueryExhausted() : Boolean{
    return getCurrentViewStateOrNew().movieListFields.isQueryExhausted
}