package com.sonu.openapi.repository.auth

import androidx.lifecycle.LiveData
import com.sonu.openapi.api.auth.OpenApiAuthService
import com.sonu.openapi.api.auth.network_responses.LoginResponse
import com.sonu.openapi.api.auth.network_responses.RegistrationResponse
import com.sonu.openapi.persistence.AccountPropertiesDao
import com.sonu.openapi.persistence.AuthTokenDao
import com.sonu.openapi.session.SessionManager
import com.sonu.openapi.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authTokenDao: AuthTokenDao,
    private val openApiAuthService: OpenApiAuthService,
    private val accountPropertiesDao: AccountPropertiesDao,
    private val sessionManager: SessionManager
) {
    fun testLoginRequest(
        email: String,
        password: String
    ): LiveData<GenericApiResponse<LoginResponse>> {
        return openApiAuthService.login(email, password)
    }

    fun testRegistrationRequest(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<GenericApiResponse<RegistrationResponse>> {
        return openApiAuthService.register(email, username, password, confirmPassword)
    }
}