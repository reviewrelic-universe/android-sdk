package com.review.relic.dto


import com.google.gson.annotations.SerializedName

data class ReviewRelicSubmitResponse(
    @SerializedName("data")
    val reviewReviewData: ReviewData,
    @SerializedName("status")
    val status: Boolean
) {
    data class ReviewData(
        @SerializedName("comments")
        val comments: String,
        @SerializedName("label")
        val label: Any,
        @SerializedName("rating")
        val rating: Int,
        @SerializedName("sku")
        val sku: String,
        @SerializedName("uuid")
        val uuid: String
    )
}