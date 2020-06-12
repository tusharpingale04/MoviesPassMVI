package com.tushar.movieappmvi.ui.main.favorites.state

sealed class FavoritesStateEvent {
    object FetchFavoritesMovies : FavoritesStateEvent()
    object None : FavoritesStateEvent()

}