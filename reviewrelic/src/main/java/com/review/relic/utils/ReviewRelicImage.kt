package com.review.relic.utils

data class ReviewRelicImage(
    var image: Any? = null,
    var type: ImageType
)

enum class ImageType {
    ResourceId,
    Base64,
    /*Url // in future*/
}