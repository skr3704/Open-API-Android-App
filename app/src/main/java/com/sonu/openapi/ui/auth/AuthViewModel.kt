package com.sonu.openapi.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sonu.openapi.api.auth.network_responses.LoginResponse
import com.sonu.openapi.api.auth.network_responses.RegistrationResponse
import com.sonu.openapi.models.AuthToken
import com.sonu.openapi.repository.auth.AuthRepository
import com.sonu.openapi.ui.BaseViewModel
import com.sonu.openapi.ui.DataState
import com.sonu.openapi.ui.auth.state.AuthStateEvent
import com.sonu.openapi.ui.auth.state.AuthStateEvent.*
import com.sonu.openapi.ui.auth.state.AuthViewState
import com.sonu.openapi.ui.auth.state.LoginFields
import com.sonu.openapi.ui.auth.state.RegistrationFields
import com.sonu.openapi.util.AbsentLiveData
import com.sonu.openapi.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {


    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {

        return when (stateEvent) {
            is LoginAttempt -> {
                authRepository.attemptLogin(stateEvent.email, stateEvent.password)
            }
            CheckPreviousAuthEvent -> {
                authRepository.checkPreviousAuthUser()
            }
            is RegisterAttemptEvent -> {
                authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )

            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }


    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrentViewStateOrNew()
        if(update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }


    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields) {
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
//        authRepository.cancelActiveJobs()
    }

    fun cancelActiveJobs() {
        authRepository.cancelActiveJobs()
    }

}