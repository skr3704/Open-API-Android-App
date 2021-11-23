package com.sonu.openapi.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.service.autofill.Dataset
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.sonu.openapi.R
import com.sonu.openapi.ui.*
import com.sonu.openapi.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class ForgotPasswordFragment : BaseAuthFragment() {

    lateinit var webView: WebView
    lateinit var stateChangedListener: DataStateChangeListener

    private val webInteractionCallBack = object : WebAppInterface.OnWebInteractionCallback {
        override fun onSuccess(email: String) {
            Log.d(TAG, "onSuccess: a reset link will sent to $email")
            onPasswordResetLink()
        }

        override fun onError(errorMessage: String) {
            val dataState =
                DataState.error<Any>(response = Response(errorMessage, ResponseType.Dialog))
            stateChangedListener.onDataStateChange(dataState)
        }

        override fun onLoading(isLoading: Boolean) {
            GlobalScope.launch(Main) {
                stateChangedListener.onDataStateChange(
                    DataState.loading(
                        isLoading = isLoading,
                        cachedData = null
                    )
                )
            }
        }
    }

    private fun onPasswordResetLink() {
        GlobalScope.launch(Main) {
            parent_view.removeView(webView)
            webView.destroy()
            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f, 0f,
                0f
            )
            animation.duration = 500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility = View.VISIBLE

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stateChangedListener.onDataStateChange(
            DataState.loading(isLoading = true, cachedData = null)
        )
        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
        Log.d(TAG, "ForgotPasswordFragment: $viewModel")
        webView = webview
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stateChangedListener.onDataStateChange(
                    DataState.loading(isLoading = false, cachedData = null)
                )
            }

        }
        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(
            WebAppInterface(
                webInteractionCallBack
            ), "AndroidTextListener"
        )
    }

    fun loadPasswordRestWebview() {
        stateChangedListener.onDataStateChange(
            DataState.loading(isLoading = true, cachedData = null)
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            stateChangedListener = context as DataStateChangeListener
        } catch (exception: ClassCastException) {
            Log.e(TAG, "onAttach: ClassCastException ${exception.message}")
        }
    }

    class WebAppInterface constructor(
        private val callback: OnWebInteractionCallback
    ) {
        private val TAG = "AppDebug"

        @JavascriptInterface
        fun onSuccess(email: String) {
            callback.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(errorMessage: String) {
            callback.onError(errorMessage)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callback.onLoading(isLoading)
        }


        interface OnWebInteractionCallback {
            fun onSuccess(email: String)
            fun onError(errorMessage: String)
            fun onLoading(isLoading: Boolean)
        }
    }
}