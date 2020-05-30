package com.tushar.movieappmvi.db

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.tushar.movieappmvi.models.Keyword
import com.tushar.movieappmvi.models.SimilarMovie

class Converters{

    @TypeConverter
    fun fromString(value: String?): List<Keyword?>? {
        val listType = object : TypeToken<List<Keyword?>?>() {}.type
        return AppDatabase.gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<Keyword?>?): String? {
        return AppDatabase.gson.toJson(list)
    }

    @TypeConverter
    fun fromMoviesString(value: String?): List<SimilarMovie?>? {
        val listType = object : TypeToken<List<SimilarMovie?>?>() {}.type
        return AppDatabase.gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromMoviesArrayList(list: List<SimilarMovie?>?): String? {
        return AppDatabase.gson.toJson(list)
    }

}