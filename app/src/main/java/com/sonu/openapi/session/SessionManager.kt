package com.sonu.openapi.session

import android.app.Application
import com.sonu.openapi.persistence.AuthTokenDao

class SessionManager constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
}