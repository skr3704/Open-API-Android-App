package com.sonu.openapi.api.main

import androidx.lifecycle.LiveData
import com.sonu.openapi.models.AccountProperties
import com.sonu.openapi.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenApiMainService {
    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>
}