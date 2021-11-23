package com.sonu.openapi.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sonu.openapi.api.auth.OpenApiAuthService
import com.sonu.openapi.api.auth.network_responses.LoginResponse
import com.sonu.openapi.api.auth.network_responses.RegistrationResponse
import com.sonu.openapi.models.AuthToken
import com.sonu.openapi.persistence.AccountPropertiesDao
import com.sonu.openapi.persistence.AuthTokenDao
import com.sonu.openapi.repository.NetworkBoundResource
import com.sonu.openapi.session.SessionManager
import com.sonu.openapi.ui.Data
import com.sonu.openapi.ui.DataState
import com.sonu.openapi.ui.Response
import com.sonu.openapi.ui.ResponseType
import com.sonu.openapi.ui.auth.state.AuthViewState
import com.sonu.openapi.ui.auth.state.LoginFields
import com.sonu.openapi.ui.auth.state.RegistrationFields
import com.sonu.openapi.ui.auth.state.RegistrationFields.RegistrationError.*
import com.sonu.openapi.util.*
import com.sonu.openapi.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authTokenDao: AuthTokenDao,
    private val openApiAuthService: OpenApiAuthService,
    private val accountPropertiesDao: AccountPropertiesDao,
    private val sessionManager: SessionManager
) {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginError = LoginFields(email, password).isValidForLogin()
        if (loginError != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginError, ResponseType.Dialog)
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                //incorrect login credentials
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                onCompleteJob(
                    dataState = DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                account_pk = response.body.pk,
                                token = response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldErrors =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if (registrationFieldErrors != RegistrationFields.RegistrationError.none()) {
            return returnErrorResponse(registrationFieldErrors, ResponseType.Dialog)
        }
        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                //incorrect login credentials
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                onCompleteJob(
                    dataState = DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                account_pk = response.body.pk,
                                token = response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    private fun returnErrorResponse(
        message: String,
        type: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        Log.e(TAG, "Error signing $message")
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    response = Response(message = message, responseType = type)
                )
            }
        }
    }

    fun cancelActiveJobs() {
        Log.e(TAG, "AuthRepo cancelling active jobs")
        repositoryJob?.cancel()
    }


}