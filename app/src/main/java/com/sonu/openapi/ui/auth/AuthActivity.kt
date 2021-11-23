package com.sonu.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sonu.openapi.R
import com.sonu.openapi.ui.BaseActivity
import com.sonu.openapi.ui.main.MainActivity
import com.sonu.openapi.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewmodel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewmodel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        subscribeObservers()


    }

    private fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, { authToken ->
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navigateToMainActiivty()
            }
        })

        viewmodel.viewState.observe(this, {
            it.authToken?.let {
                sessionManager.login(it)
            }
        })

    }

    private fun navigateToMainActiivty() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}