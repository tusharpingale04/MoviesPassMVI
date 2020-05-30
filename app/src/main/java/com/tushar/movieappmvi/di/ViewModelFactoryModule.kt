package com.tushar.movieappmvi.di

import androidx.lifecycle.ViewModelProvider
import com.tushar.movieappmvi.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule{

    @Binds
    abstract fun provideViewModelFactory(factory: ViewModelProviderFactory) : ViewModelProvider.Factory
}