package com.sonu.openapi.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.sonu.openapi.R
import com.sonu.openapi.ui.BaseActivity
import com.sonu.openapi.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeObservers()
        tool_bar.setOnClickListener { sessionManager.logout() }

    }

    private fun subscribeObservers() {
        sessionManager.cachedToken.observe(this,{authToken ->
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null){
                navigateToAuthActivity()
            }
        })
    }

    private fun navigateToAuthActivity() {
        startActivity(Intent(this,AuthActivity::class.java))
        finish()
    }
}