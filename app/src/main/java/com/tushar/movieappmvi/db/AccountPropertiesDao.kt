package com.tushar.movieappmvi.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tushar.movieappmvi.models.AccountProperties

@Dao
interface AccountPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountProperties: AccountProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties): Long

    @Query("SELECT * FROM account_properties WHERE id =:id")
    fun searchById(id: Int): LiveData<AccountProperties>

    @Query("SELECT * FROM account_properties WHERE username = :username")
    suspend fun searchByUserName(username: String): AccountProperties?

    @Query("UPDATE account_properties SET username = :username WHERE id = :id ")
    fun updateAccountProperties(id: Int, username: String)
}