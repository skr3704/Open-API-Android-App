package com.sonu.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.sonu.openapi.R
import com.sonu.openapi.ui.BaseActivity
import com.sonu.openapi.ui.ResponseType
import com.sonu.openapi.ui.main.MainActivity
import com.sonu.openapi.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener {

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
        viewmodel.dataState.observe(this, { dataState ->
            onDataStateChange(dataState)
            Log.d(TAG, "authActivity DataState: $dataState")
            dataState.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            viewmodel.setAuthToken(it)
                        }
                    }
                }
            }
        })


        viewmodel.viewState.observe(this, {
            it.authToken?.let {
                sessionManager.login(it)
            }
        })

        sessionManager.cachedToken.observe(this, { authToken ->
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navigateToMainActivity()
            }
        })


    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        //error
//        viewmodel.cancelActiveJobs()
    }


    override fun displayProgressBar(bool: Boolean) {
        if (bool) progress_bar.visibility = View.VISIBLE else progress_bar.visibility =
            View.INVISIBLE
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}