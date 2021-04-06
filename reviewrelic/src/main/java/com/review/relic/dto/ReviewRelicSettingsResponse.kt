package com.review.relic.dto


import com.google.gson.annotations.SerializedName

data class ReviewRelicSettingsResponse(
    @SerializedName("data")
    val dataResponse: ReviewRelicDataResponse
) {

    data class ReviewRelicDataResponse(
        @SerializedName("merchant-id")
        val merchantId: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("review-settings")
        val reviewSettings: List<ReviewSetting>,
        @SerializedName("settings")
        val settings: Settings
    ) {
        data class ReviewSetting(
            @SerializedName("label")
            val label: String,
            @SerializedName("value")
            val value: Int
        )

        data class Settings(
            @SerializedName("app-logo")
            val appLogo: String,
            @SerializedName("can-skip")
            val canSkip: Boolean,
            @SerializedName("empty-image")
            val emptyImage: String,
            @SerializedName("fill-image")
            val fillImage: String,
            @SerializedName("should-show-images")
            val shouldShowImages: Boolean,
            @SerializedName("type")
            val type: Int,
            @SerializedName("color")
            val color: String
        )
    }
}