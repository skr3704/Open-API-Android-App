package com.sonu.openapi.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sonu.openapi.models.AccountProperties
import com.sonu.openapi.models.AuthToken

@Database(entities = [AccountProperties::class, AuthToken::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}