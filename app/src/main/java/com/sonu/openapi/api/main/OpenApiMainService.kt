package com.sonu.openapi.api.main

import androidx.lifecycle.LiveData
import com.sonu.openapi.api.GenericResponse
import com.sonu.openapi.models.AccountProperties
import com.sonu.openapi.util.GenericApiResponse
import retrofit2.http.*

interface OpenApiMainService {
    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): LiveData<GenericApiResponse<GenericResponse>>
}