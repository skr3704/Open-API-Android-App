package com.sonu.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sonu.openapi.api.main.OpenApiMainService
import com.sonu.openapi.models.AccountProperties
import com.sonu.openapi.models.AuthToken
import com.sonu.openapi.persistence.AccountPropertiesDao
import com.sonu.openapi.repository.NetworkBoundResource
import com.sonu.openapi.session.SessionManager
import com.sonu.openapi.ui.DataState
import com.sonu.openapi.ui.main.account.state.AccountViewState
import com.sonu.openapi.util.ApiSuccessResponse
import com.sonu.openapi.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {

    private val TAG: String = "AppDebug"

    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true
            ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                accountPropertiesDao.insertAndReplace(response.body)
                createCacheReqestAndReturn() //**
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService
                    .getAccountProperties(
                        "Token ${authToken.token!!}"
                    )
            }

            override suspend fun createCacheReqestAndReturn() {
                //**
                withContext(Main) {
                    result.addSource(loadFromCache()) {
                        onCompleteJob(
                            DataState.data(data = it, response = null)
                        )
                    }
                }
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap { accountProperties ->
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(accountProperties)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                cacheObject?.let {
                    accountPropertiesDao.insertAndReplace(it)
                }
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }


        }.asLiveData()
    }


    fun cancelActiveJobs() {
        Log.d(TAG, "AuthRepository: Cancelling on-going jobs...")
        repositoryJob?.cancel()
    }
}
