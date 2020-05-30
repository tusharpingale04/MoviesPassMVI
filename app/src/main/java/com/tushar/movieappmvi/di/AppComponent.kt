package com.tushar.movieappmvi.di

import android.app.Application
import com.google.gson.Gson
import com.tushar.movieappmvi.BaseApplication
import com.tushar.movieappmvi.session.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    ActivityBuildersModule::class,
    ViewModelFactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<BaseApplication> {

    val sessionManager: SessionManager

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}