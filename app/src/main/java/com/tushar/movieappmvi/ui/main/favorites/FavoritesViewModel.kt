package com.tushar.movieappmvi.ui.main.favorites

import androidx.lifecycle.LiveData
import com.tushar.movieappmvi.models.FavoriteMovie
import com.tushar.movieappmvi.models.SimilarMovie
import com.tushar.movieappmvi.repository.main.FavoritesRepository
import com.tushar.movieappmvi.session.SessionManager
import com.tushar.movieappmvi.ui.BaseViewModel
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.main.favorites.state.FavoritesStateEvent
import com.tushar.movieappmvi.ui.main.favorites.state.FavoritesViewState
import com.tushar.movieappmvi.ui.main.movies.state.MovieStateEvent
import com.tushar.movieappmvi.ui.main.movies.viewmodel.MoviesViewModel
import com.tushar.movieappmvi.util.AbsentLiveData
import javax.inject.Inject

class FavoritesViewModel @Inject
constructor(
    val sessionManager: SessionManager,
    private val favoritesRepository: FavoritesRepository
) : BaseViewModel<FavoritesStateEvent, FavoritesViewState>(){
    override fun getViewState() = FavoritesViewState ()

    override fun handleStateEvent(stateEvent: FavoritesStateEvent): LiveData<DataState<FavoritesViewState>> {
        return when(stateEvent){
            is FavoritesStateEvent.FetchFavoritesMovies ->{
                val authToken = sessionManager.cachedToken.value
                if(authToken?.token != null){
                    favoritesRepository.fetchFavoriteMovies(authToken.token)
                }else{
                    AbsentLiveData.create()
                }
            }
            is FavoritesStateEvent.None ->{
                AbsentLiveData.create()
            }
        }
    }

    fun cancelActiveJobs() {
        setStateEvent(FavoritesStateEvent.None)
        favoritesRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun setFavoritesMovies(movieList: List<FavoriteMovie>) {
        val update = getCurrentViewStateOrNew()
        if (movieList == update.movieList) {
            return
        }
        update.movieList = movieList
        setViewState(update)
    }

}