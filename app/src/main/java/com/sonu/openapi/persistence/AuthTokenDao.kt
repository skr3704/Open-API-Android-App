package com.sonu.openapi.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sonu.openapi.models.AuthToken

@Dao
interface AuthTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(token: AuthToken): Long

    @Query("UPDATE AUTH_TOKEN SET token = null WHERE account_pk = :pk")
    fun update(pk: Int)

}