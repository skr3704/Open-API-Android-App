package com.sonu.openapi.session

import android.app.Application
import com.sonu.openapi.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
}