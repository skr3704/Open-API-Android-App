package com.sonu.openapi.ui.auth

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
                AbsentLiveData.create()
            }
            CheckPreviousAuthEvent -> {
                AbsentLiveData.create()
            }
            is RegisterAttemptEvent -> {
                AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setLoginFields(loginFields: LoginFields) {
        val state = getCurrentViewStateOrNew()
        val value = if (state.loginFields == loginFields) return else loginFields
        state.loginFields = loginFields
        _viewState.value = state
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        val update = getCurrentViewStateOrNew()
        if(update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

}