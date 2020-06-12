package com.tushar.movieappmvi.ui.main.favorites.state

import com.tushar.movieappmvi.models.FavoriteMovie

data class FavoritesViewState(
    var movieList: List<FavoriteMovie> = ArrayList()
)

