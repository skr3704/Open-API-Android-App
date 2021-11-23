package com.sonu.openapi.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sonu.openapi.api.auth.OpenApiAuthService
import com.sonu.openapi.api.auth.network_responses.LoginResponse
import com.sonu.openapi.api.auth.network_responses.RegistrationResponse
import com.sonu.openapi.models.AccountProperties
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
import com.sonu.openapi.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.sonu.openapi.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.sonu.openapi.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authTokenDao: AuthTokenDao,
    private val openApiAuthService: OpenApiAuthService,
    private val accountPropertiesDao: AccountPropertiesDao,
    private val sessionManager: SessionManager,
    private val sharedPreferences: SharedPreferences,
    private val sharedPrefEditor: SharedPreferences.Editor
) {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginError = LoginFields(email, password).isValidForLogin()
        if (loginError != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginError, ResponseType.Dialog)
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true
        ) {
            //not used in this case
            override suspend fun createCacheReqestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                //incorrect login credentials
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //insert accountProperties into db for autoken to save
                val accountInsert = accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        pk = response.body.pk,
                        email = response.body.email, username = ""
                    )
                )
                Log.e(TAG, "account data insert $accountInsert")

                //return -1 if not inserted successfully
                val result = authTokenDao.insert(
                    AuthToken(
                        account_pk = response.body.pk,
                        token = response.body.token
                    )
                )
                Log.e(TAG, "tokenInsert $result")

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog)
                        )
                    )
                }
                saveAuthenticatedUserToPref(email)

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
            sessionManager.isConnectedToTheInternet(),
            true
        ) {
            //not used in this case
            override suspend fun createCacheReqestAndReturn() {
                TODO("Not yet implemented")
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                //incorrect login credentials
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                //insert accountProperties into db for autoken to save
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        pk = response.body.pk,
                        email = response.body.email, username = ""
                    )
                )

                //return -1 if not inserted successfully
                val result = authTokenDao.insert(
                    AuthToken(
                        account_pk = response.body.pk,
                        token = response.body.token
                    )
                )
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog)
                        )
                    )
                }

                saveAuthenticatedUserToPref(email)



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

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUserEmail: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if (previousAuthUserEmail.isNullOrBlank()) {
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found")
            return returnNoTokenFound()
        }
        return object : NetworkBoundResource<Void, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false
        ) {
            //not  used in  this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
            }

            //not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override suspend fun createCacheReqestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                    Log.d(TAG, "checkPrevAuthUser: searching for token  $accountProperties")
                    accountProperties?.let {
                        if (accountProperties.pk > -1) {
                            authTokenDao.searchByPk(accountProperties.pk)?.let { authToken ->
                                onCompleteJob(
                                    DataState.data(
                                        data = AuthViewState(authToken = authToken)
                                    )
                                )
                                return
                            }
                        }
                    }
                    Log.d(TAG, "checkPrevAuthUser: AuthToken not found...")
                    onCompleteJob(
                        DataState.data(
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None

                            )
                        )
                    )

                }
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }


        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None)
                )
            }
        }
    }

    private fun saveAuthenticatedUserToPref(email: String) {
        sharedPrefEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefEditor.apply()

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