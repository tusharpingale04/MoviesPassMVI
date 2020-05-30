package com.tushar.movieappmvi.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.api.ApiKeyInterceptor
import com.tushar.movieappmvi.db.AccountPropertiesDao
import com.tushar.movieappmvi.db.AppDatabase
import com.tushar.movieappmvi.db.AuthTokenDao
import com.tushar.movieappmvi.util.Constants
import com.tushar.movieappmvi.util.LiveDataCallAdapterFactory
import com.tushar.movieappmvi.util.PreferenceKeys
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application, gson: Gson) : AppDatabase{
        return Room.databaseBuilder(application, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build().apply {
                AppDatabase.gson = gson
            }
    }

    @Singleton
    @Provides
    fun provideAccountProperties(db: AppDatabase) : AccountPropertiesDao{
        return db.getAccountProperties()
    }

    @Singleton
    @Provides
    fun provideAuthToken(db: AppDatabase) : AuthTokenDao{
        return db.getAuthToken()
    }

    @Singleton
    @Provides
    fun provideRequestOptions() : RequestOptions{
        return RequestOptions().error(R.drawable.default_image).placeholder(R.drawable.default_image)
    }

    @Singleton
    @Provides
    fun provideRequestManager(application: Application,requestOptions: RequestOptions) : RequestManager{
        return Glide.with(application).setDefaultRequestOptions(requestOptions)
    }

    @Singleton
    @Provides
    fun provideApiKeyInterceptor() : ApiKeyInterceptor{
        return ApiKeyInterceptor()
    }

    @Singleton
    @Provides
    fun provideOkHttpClientWithInterceptor(apiKeyInterceptor: ApiKeyInterceptor) : OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(apiKeyInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gson: Gson, okHttpClient: OkHttpClient) : Retrofit.Builder{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application) : SharedPreferences{
        return application.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesEditor(sharedPreferences: SharedPreferences) : SharedPreferences.Editor{
        return sharedPreferences.edit()
    }
}