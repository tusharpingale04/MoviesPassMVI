package com.tushar.movieappmvi.di.main

import com.tushar.movieappmvi.ui.main.account.AccountDetailFragment
import com.tushar.movieappmvi.ui.main.favorites.FavoritesFragment
import com.tushar.movieappmvi.ui.main.favorites.WatchListFragment
import com.tushar.movieappmvi.ui.main.movies.MovieDetailsFragment
import com.tushar.movieappmvi.ui.main.movies.MovieListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeAccountDetailsFragment() : AccountDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoritesFragment() : FavoritesFragment

    @ContributesAndroidInjector
    abstract fun contributeWatchListFragment() : WatchListFragment

    @ContributesAndroidInjector
    abstract fun contributeMoviesListFragment() : MovieListFragment

    @ContributesAndroidInjector
    abstract fun contributeMovieDetailsFragment() : MovieDetailsFragment
}