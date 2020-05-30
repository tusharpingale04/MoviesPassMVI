package com.tushar.movieappmvi.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tushar.movieappmvi.models.AuthToken

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authToken: AuthToken) : Long

    @Query("UPDATE auth_table SET session_id = null WHERE account_id = :pk")
    fun nullifyToken(pk: Int): Int

    @Query("SELECT * FROM auth_table WHERE account_id = :pk")
    suspend fun searchById(pk: Int): AuthToken?

}