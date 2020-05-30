package com.tushar.movieappmvi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.tushar.movieappmvi.models.*
import javax.inject.Inject

@Database(entities = [AccountProperties::class,AuthToken::class,
    Movie::class, KeywordModel::class, SimilarMoviesModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun getAccountProperties() : AccountPropertiesDao
    abstract fun getAuthToken() : AuthTokenDao
    abstract fun getMovie() : MovieDao

    companion object{
        const val DATABASE_NAME = "movie_db"
        lateinit var gson: Gson
    }

}