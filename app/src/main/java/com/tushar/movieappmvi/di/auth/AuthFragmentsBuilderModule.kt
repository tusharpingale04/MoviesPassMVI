package com.tushar.movieappmvi.di.auth

import com.tushar.movieappmvi.ui.auth.LauncherFragment
import com.tushar.movieappmvi.ui.auth.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract fun provideLauncherFragment() : LauncherFragment

    @ContributesAndroidInjector
    abstract fun provideLoginFragment() : LoginFragment

}