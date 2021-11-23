package com.sonu.openapi.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.sonu.openapi.ui.DataState
import com.sonu.openapi.ui.Response
import com.sonu.openapi.ui.ResponseType
import com.sonu.openapi.util.*
import com.sonu.openapi.util.Constants.Companion.NETWORK_TIMEOUT
import com.sonu.openapi.util.Constants.Companion.TESTING_CACHE_DELAY
import com.sonu.openapi.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.sonu.openapi.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.sonu.openapi.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.sonu.openapi.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.sonu.openapi.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, ViewStateType>(
    isNetworkAvailable: Boolean,
    isNetworkRequest: Boolean
) {
    private val TAG = "AppDebug"
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    private lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))
        if (isNetworkRequest) {
            if (isNetworkAvailable) {
                coroutineScope.launch {
                    //simulate a network delay for  testing

                    delay(TESTING_NETWORK_DELAY)
                    withContext(Main) {
                        val apiResponse = createCall()
                        result.addSource(apiResponse) { response ->
                            result.removeSource(apiResponse)
                            coroutineScope.launch {
                                handleNetworkCall(response)
                            }
                        }
                    }
                }
                GlobalScope.launch(IO) {
                    delay(NETWORK_TIMEOUT)
                    if (!job.isCompleted) {
                        job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
                    }
                }
            } else {
                onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, true, false)
            }
        } else {
            coroutineScope.launch {
                //fake delay for testing
                delay(TESTING_CACHE_DELAY)

                //view data from cache only and return
                createCacheReqestAndReturn()
            }
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204)")
                onErrorReturn("HTTP 204. Returned nothing.", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None
        if (msg == null) {
            msg = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast

        }
        if (useDialog) {
            responseType = ResponseType.Dialog
        }

        onCompleteJob(
            DataState.error(
                response = Response(
                    msg, responseType
                )
            )
        )
    }

    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: :called...")
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e(TAG, "NetworkBoundResource job is cancelled")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource job is completed")
                        //do nothing should be handled already
                    }
                }
            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
    abstract suspend fun createCacheReqestAndReturn()

    abstract fun setJob(job: Job)
}