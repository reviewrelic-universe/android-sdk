package com.review.relic

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.gson.JsonObject
import com.review.relic.dto.ReviewRelicSettingsResponse
import com.review.relic.dto.ReviewRelicSubmitResponse
import com.review.relic.ui.ReviewRelicBottomSheet
import com.review.relic.ui.ReviewRelicBottomSheetInputs
import com.review.relic.utils.hmacEncryption
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ReviewRelic {
    private const val url = "https://reviewrelic.com/api/v1/"
    private var token: String? = null
    private var secretKey: String? = null
    private var merchantId: String? = null
    private var enableLogging: Boolean = false

    private var reviewRelicService: ReviewRelicService? = null

    var reviewRelicSettings: ReviewRelicSettingsResponse.ReviewRelicDataResponse? = null
    var onInitializedListener: ReviewRelicOnInitializedListener? = null

    /**
     * Set the listener for initialization
     */
    fun setInitializeListener(onInitializedListener: ReviewRelicOnInitializedListener): ReviewRelic {
        this.onInitializedListener = onInitializedListener
        return this
    }

    /**
     * Set the environment API key
     *
     * @param secretKey the secret key for environment
     * @return the Builder
     */
    fun setSecretKey(secretKey: String?): ReviewRelic {
        return if (null == secretKey) {
            throw IllegalArgumentException("Token can not be null")
        } else {
            this.secretKey = secretKey
            this
        }
    }

    /**
     * Enables logging, the project importing this module must include an implementation slf4j in their pom.
     *
     * @param enableLogging log error level.
     * @return the Builder
     */
    fun enableLogging(enableLogging: Boolean): ReviewRelic {
        this.enableLogging = enableLogging
        return this
    }

    /**
     * Set the environment API key
     *
     * @param token the token for environment
     * @return the Builder
     */
    fun setToken(token: String?): ReviewRelic {
        return if (null == token) {
            throw IllegalArgumentException("Token can not be null")
        } else {
            this.token = token
            this
        }
    }

    /**
     * Set the environment merchantId
     *
     * @param merchantId the merchantId for environment
     * @return the Builder
     */
    fun setMerchantId(merchantId: String?): ReviewRelic {
        return if (null == merchantId) {
            throw IllegalArgumentException("Merchant Id can not be null")
        } else {
            this.merchantId = merchantId
            this
        }
    }

    fun build() {
        val httpClient: OkHttpClient.Builder =
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original: Request = chain.request()
                    val request = original.newBuilder().headers(original.headers())
                        .header("Authorization", "Bearer $token")
                        .method(original.method(), original.body())
                    chain.proceed(request.build())
                }
        if (enableLogging) {
            val logging = HttpLoggingInterceptor()
            httpClient.addInterceptor(logging)
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        reviewRelicService = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build()).build()
            .create(ReviewRelicService::class.java)

        if (reviewRelicSettings == null)
            fetchSettings()
        else
            onInitializedListener?.onInitializationComplete(true)
    }

    private fun fetchSettings() {
        reviewRelicService?.getSettings(merchantId)
            ?.enqueue(object : Callback<ReviewRelicSettingsResponse> {
                override fun onResponse(
                    call: Call<ReviewRelicSettingsResponse>,
                    response: Response<ReviewRelicSettingsResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        reviewRelicSettings = body?.dataResponse
                        onInitializedListener?.onInitializationComplete(true)
                    } else {
                        onInitializedListener?.onInitializationComplete(false)
                        throw UninitializedPropertyAccessException("Review Relic initialization error -- Code: ${response.code()} , Response: $response")
                    }
                }

                override fun onFailure(
                    call: Call<ReviewRelicSettingsResponse>,
                    throwable: Throwable
                ) {
                    onInitializedListener?.onInitializationComplete(false)
                    throw UninitializedPropertyAccessException("Review Relic initialization error -- Message: ${throwable.message}")
                }

            })
    }

    fun showSheet(
        transactionId: String? = null, thankYouMessage: String? = null,
        reviewRelicBottomSheetInputs: ReviewRelicBottomSheetInputs? = null,
        fragmentManager: FragmentManager
    ) {
        ReviewRelicBottomSheet(
            transactionId = transactionId,
            thankYouMessage = thankYouMessage,
            reviewRelicBottomSheetInputs = reviewRelicBottomSheetInputs
        ) {
        }.show(
            fragmentManager,
            ReviewRelicBottomSheet::class.java.name
        )
    }

    fun submitReview(
        jsonObject: JsonObject,
        comment: String?,
        title: String?,
        subtitle: String?,
        callback: (ReviewRelicSubmitResponse?) -> Unit
    ) {
        val jsonBody = jsonObject.deepCopy()
        jsonBody.addProperty("comments", comment)
        jsonBody.addProperty("title", title)
        jsonBody.addProperty("subtitle", subtitle)
        reviewRelicService?.submitReview(
            bearerToken = "Bearer $token",
            jsonObject = jsonBody,
            merchantId = merchantId,
            hmacSignature = hmacEncryption(jsonObject.toString(), secretKey!!),
        )?.enqueue(object : Callback<ReviewRelicSubmitResponse> {
            override fun onResponse(
                call: Call<ReviewRelicSubmitResponse>,
                response: Response<ReviewRelicSubmitResponse>
            ) {
                if (response.isSuccessful) {
                    callback.invoke(response.body())
                } else {
                    callback.invoke(null)
                    if (enableLogging)
                        Log.v(
                            "submit issue",
                            "Review Relic submit error -- Code: ${response.code()} , Response: $response"
                        )
                }
            }

            override fun onFailure(call: Call<ReviewRelicSubmitResponse>, throwable: Throwable) {
                callback.invoke(null)
                if (enableLogging)
                    Log.v(
                        "submit issue",
                        "Review Relic submit error -- Message: ${throwable.message}"
                    )
            }

        })
    }

}

interface ReviewRelicOnInitializedListener {
    fun onInitializationComplete(isSuccessful: Boolean)
}