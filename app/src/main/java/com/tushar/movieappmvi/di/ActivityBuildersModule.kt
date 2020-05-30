package com.tushar.movieappmvi.di

import com.tushar.movieappmvi.di.auth.AuthFragmentsBuilderModule
import com.tushar.movieappmvi.di.auth.AuthModule
import com.tushar.movieappmvi.di.auth.AuthScope
import com.tushar.movieappmvi.di.auth.AuthViewModelModule
import com.tushar.movieappmvi.di.main.MainFragmentsBuilderModule
import com.tushar.movieappmvi.di.main.MainModule
import com.tushar.movieappmvi.di.main.MainScope
import com.tushar.movieappmvi.di.main.MainViewModelModule
import com.tushar.movieappmvi.ui.auth.AuthActivity
import com.tushar.movieappmvi.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [
            AuthFragmentsBuilderModule::class,
            AuthModule::class,
            AuthViewModelModule::class
        ]
    )
    abstract fun provideAuthActivity() : AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [
        MainFragmentsBuilderModule::class,
        MainModule::class,
        MainViewModelModule::class
        ]
    )
    abstract fun provideMainActivity() : MainActivity
}