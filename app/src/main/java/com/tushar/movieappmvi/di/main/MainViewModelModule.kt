package com.tushar.movieappmvi.di.main

import androidx.lifecycle.ViewModel
import com.tushar.movieappmvi.ui.main.account.AccountViewModel
import com.tushar.movieappmvi.ui.main.favorites.FavoritesViewModel
import com.tushar.movieappmvi.ui.main.movies.viewmodel.MoviesViewModel
import com.tushar.movieappmvi.viewmodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule{

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoviesViewModel::class)
    abstract fun bindMovieViewModel(moviesViewModel: MoviesViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesViewModel::class)
    abstract fun bindFavoritesViewModel(favoritesViewModel: FavoritesViewModel) : ViewModel
}