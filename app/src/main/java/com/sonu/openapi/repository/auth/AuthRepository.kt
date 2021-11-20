package com.sonu.openapi.repository.auth

import com.sonu.openapi.api.auth.OpenApiAuthService
import com.sonu.openapi.persistence.AccountPropertiesDao
import com.sonu.openapi.persistence.AuthTokenDao
import com.sonu.openapi.session.SessionManager

class AuthRepository constructor(
    authTokenDao: AuthTokenDao,
    openApiAuthService: OpenApiAuthService,
    accountPropertiesDao: AccountPropertiesDao,
    sessionManager: SessionManager
) {
}