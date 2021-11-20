package com.sonu.openapi.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.sonu.openapi.R
import com.sonu.openapi.ui.BaseActivity
import kotlinx.android.synthetic.main.fragment_launcher.*

class AuthActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}