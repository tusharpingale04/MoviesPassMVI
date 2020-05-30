package com.tushar.movieappmvi.di.auth

import androidx.lifecycle.ViewModel
import com.tushar.movieappmvi.ui.auth.AuthViewModel
import com.tushar.movieappmvi.viewmodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel) : ViewModel

}