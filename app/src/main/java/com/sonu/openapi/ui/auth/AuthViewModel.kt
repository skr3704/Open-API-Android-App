package com.sonu.openapi.ui.auth

import androidx.lifecycle.ViewModel
import com.sonu.openapi.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel() {

}