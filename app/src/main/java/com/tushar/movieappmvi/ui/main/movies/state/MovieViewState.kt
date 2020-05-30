package com.tushar.movieappmvi.ui.main.movies.state

import com.tushar.movieappmvi.models.Keyword
import com.tushar.movieappmvi.models.Movie
import com.tushar.movieappmvi.models.SimilarMovie

data class MovieViewState (
    var movieListFields: MovieListFields = MovieListFields(),//For MovieList Page
    var movieFields: MovieFields = MovieFields()
){
    data class MovieListFields(
        var movieList: List<Movie> = ArrayList(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )

    data class MovieFields(
        var movieDetail: Movie? = null,
        var keywordsList : List<Keyword>? = ArrayList(),
        var similarMoviesList : List<SimilarMovie>? = ArrayList()
    )
}