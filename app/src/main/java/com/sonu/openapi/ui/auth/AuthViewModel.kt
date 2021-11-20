package com.sonu.openapi.ui.auth

import androidx.lifecycle.ViewModel
import com.sonu.openapi.repository.auth.AuthRepository

class AuthViewModel constructor(
    val authRepository: AuthRepository
) : ViewModel() {

}