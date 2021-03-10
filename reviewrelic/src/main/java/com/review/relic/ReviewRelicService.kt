package com.review.relic

import com.google.gson.JsonObject
import com.review.relic.dto.ReviewRelicSettingsResponse
import com.review.relic.dto.ReviewRelicSubmitResponse
import retrofit2.Call
import retrofit2.http.*

interface ReviewRelicService {

    @GET("settings")
    fun getSettings(@Query("merchant_id") merchantId: String?): Call<ReviewRelicSettingsResponse>

    @POST("store")
    fun submitReview(
        @Header("Relic-Signature") hmacSignature: String,
        @Header("Authorization") bearerToken: String,
        @Query("merchant_id") merchantId: String?,
        @Body jsonObject: JsonObject
    ): Call<ReviewRelicSubmitResponse>

}