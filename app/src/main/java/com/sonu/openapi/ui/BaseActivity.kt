package com.sonu.openapi.ui

import android.util.Log
import com.sonu.openapi.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener {


    val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {
        GlobalScope.launch(Main) {
            dataState?.let { state ->
                state.loading?.let {
                    displayProgressBar(it.isLoading)
                }
                state.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                    displayProgressBar(false)
                }
                state.data?.let { event ->
                    event.response?.let {
                        handleStateResponse(it)
                        displayProgressBar(false)
                    }
                }

            }
        }

    }


    private fun handleStateResponse(event: Event<Response>) {
        event.getContentIfNotHandled()?.let {
            when (it.responseType) {
                is ResponseType.Dialog -> {
                    it.message?.let {
                        displaySuccessDialog(it)
                        displayProgressBar(false)
                    }

                }
                is ResponseType.Toast -> {
                    it.message?.let {
                        displayToast(it)
                        displayProgressBar(false)
                    }
                }
                is ResponseType.None -> {
                    displayProgressBar(false)
                    Log.e(TAG, "handleStateError: error ${it.message}")
                }
            }
        }
    }


    private fun handleStateError(errorEvent: Event<StateError>) {
        errorEvent.getContentIfNotHandled()?.let {
            when (it.response.responseType) {
                is ResponseType.Dialog -> {
                    it.response.message?.let {
                        displayErrorDialog(it)
                    }

                }
                is ResponseType.Toast -> {
                    it.response.message?.let {
                        displayToast(it)
                    }
                }
                is ResponseType.None -> {
                    Log.e(TAG, "handleStateError: error ${it.response.message}")
                }
            }
        }
    }

    abstract fun displayProgressBar(bool: Boolean)

}











