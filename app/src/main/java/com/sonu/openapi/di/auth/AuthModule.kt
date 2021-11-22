package com.sonu.openapi.di.auth

import com.sonu.openapi.api.auth.OpenApiAuthService
import com.sonu.openapi.persistence.AccountPropertiesDao
import com.sonu.openapi.persistence.AuthTokenDao
import com.sonu.openapi.repository.auth.AuthRepository
import com.sonu.openapi.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {
    @AuthScope
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }


    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            openApiAuthService,
            accountPropertiesDao,
            sessionManager
        )
    }

}