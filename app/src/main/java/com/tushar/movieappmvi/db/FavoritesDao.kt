package com.tushar.movieappmvi.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tushar.movieappmvi.models.FavoriteMovie

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie : FavoriteMovie?) : Long

    @Query("SELECT * from favorite_table")
    fun getFavoritesMovies() : LiveData<List<FavoriteMovie>>

}