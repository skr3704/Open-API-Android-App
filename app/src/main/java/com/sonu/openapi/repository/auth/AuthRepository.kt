package com.sonu.openapi.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sonu.openapi.api.auth.OpenApiAuthService
import com.sonu.openapi.api.auth.network_responses.LoginResponse
import com.sonu.openapi.api.auth.network_responses.RegistrationResponse
import com.sonu.openapi.models.AuthToken
import com.sonu.openapi.persistence.AccountPropertiesDao
import com.sonu.openapi.persistence.AuthTokenDao
import com.sonu.openapi.session.SessionManager
import com.sonu.openapi.ui.Data
import com.sonu.openapi.ui.DataState
import com.sonu.openapi.ui.Response
import com.sonu.openapi.ui.ResponseType
import com.sonu.openapi.ui.auth.state.AuthViewState
import com.sonu.openapi.util.*
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authTokenDao: AuthTokenDao,
    private val openApiAuthService: OpenApiAuthService,
    private val accountPropertiesDao: AccountPropertiesDao,
    private val sessionManager: SessionManager
) {

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.login(email, password)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = AuthViewState(
                                        authToken = AuthToken(
                                            token = response.body.token,
                                            account_pk = response.body.pk
                                        )
                                    )
                                )
                            }

                            is ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }

                            is ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ErrorHandling.ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }

                        }
                    }
                }
            }

    }

    fun attemptRegistration(
        email: String,
        username: String ,
        password: String,
        confirmPassword : String
    ): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.register(email, username,password,confirmPassword)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = AuthViewState(
                                        authToken = AuthToken(
                                            token = response.body.token,
                                            account_pk = response.body.pk
                                        )
                                    )
                                )
                            }

                            is ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }

                            is ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ErrorHandling.ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }

                        }
                    }
                }
            }

    }

}