package com.review.relic.viewmodel

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.review.relic.ReviewRelic
import com.review.relic.ui.ReviewRelicBottomSheetInputs
import com.review.relic.utils.getUnixTimePlus5min

class ReviewRelicSheetViewModel : ViewModel() {

    var selectedColor: Int? = null
    var transactionId: String? = null
    var thankYouMessage: String? = null
    var comments: MutableLiveData<String> = MutableLiveData()
    var checkedPosition: Int = -1
    lateinit var reviewRelicBottomSheetInputs : ReviewRelicBottomSheetInputs

    init {
        selectedColor = ReviewRelic.reviewRelicSettings?.settings?.color?.let { colorString ->
            Color.parseColor(colorString)
        }
    }

    fun getBodyForSubmit(): JsonObject {
        return JsonObject().apply {
            transactionId?.let {
                addProperty("transaction-id", it)
            }
            addProperty(
                "rating",
                ReviewRelic.reviewRelicSettings?.reviewSettings?.get(checkedPosition)?.value
            )
            addProperty("time", getUnixTimePlus5min())
        }
    }
}