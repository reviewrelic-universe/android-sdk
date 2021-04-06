package com.reviewrelic.reviewrelic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.review.relic.ReviewRelic
import com.review.relic.ui.ReviewRelicBottomSheet
import com.review.relic.ui.ReviewRelicBottomSheetInputs
import com.review.relic.utils.ImageType
import com.review.relic.utils.ReviewRelicImage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatButton>(R.id.accessibility_action_clickable_span).setOnClickListener {
            ReviewRelic
                .setSecretKey("") //TODO:/* Secret key obtained from admin panel of review relic */
                .setToken("") //TODO:/* Token obtained from admin panel of review relic */
                .setMerchantId("") //TODO:/* Merchant Id obtained from admin panel of review relic */
                .enableLogging(true)
                .build()
        }

        findViewById<AppCompatButton>(R.id.click).setOnClickListener {
            ReviewRelic.showSheet(
                transactionId = "", //TODO: /*Test Transaction Id*/
                thankYouMessage = "", //TODO:/*Test Thank you message*/
                reviewRelicBottomSheetInputs = ReviewRelicBottomSheetInputs(
                    title = "", //TODO: /*Title you want to show (will override the title set on admin panel)*/
                    subtitle = "", //TODO: /*Subtitle  you want to show (will override the subtitle set on admin panel)*/
                    image = null //TODO: /*Icon you want to show (will override the icon set on admin panel)*/
                ),
                fragmentManager = supportFragmentManager
            )
        }


    }
}