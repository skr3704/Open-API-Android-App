package com.sonu.openapi.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sonu.openapi.R
import com.sonu.openapi.util.ApiEmptyResponse
import com.sonu.openapi.util.ApiErrorResponse
import com.sonu.openapi.util.ApiSuccessResponse

class RegisterFragment : BaseAuthFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "RegisterFragment: $viewModel")
        viewModel.testRegister().observe(viewLifecycleOwner, Observer { response ->

            when(response){
                is ApiSuccessResponse ->{
                    Log.d(TAG, "REGISTER RESPONSE: ${response.body}")
                }
                is ApiErrorResponse ->{
                    Log.d(TAG, "REGISTER RESPONSE: ${response.errorMessage}")
                }
                is ApiEmptyResponse ->{
                    Log.d(TAG, "REGISTER RESPONSE: Empty Response")
                }
            }
        })

    }
}