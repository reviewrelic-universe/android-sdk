package com.review.relic

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ScrollToAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.review.relic.adapter.DataBoundViewHolder
import com.review.relic.databinding.ReviewItemLayoutBinding
import com.review.relic.ui.ReviewRelicBottomSheet
import com.review.relic.ui.TestActivity
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ReviewRelicBottomSheetTest {

    @get:Rule
    val activityTestRule = ActivityScenarioRule(TestActivity::class.java)

    private val thankYouMessage = "" //TODO:/*Test Thank you message*/
    private val transactionId = "" //TODO:/*Test Transaction Id*/

    @Before
    fun setUp() {
        ReviewRelic
            .setSecretKey("") //TODO:/* Secret key obtained from admin panel of review relic */
            .setToken("") //TODO:/* Token obtained from admin panel of review relic */
            .setMerchantId("") //TODO:/* Merchant Id obtained from admin panel of review relic */
            .enableLogging(true)
            .setInitializeListener(object : ReviewRelicOnInitializedListener {
                override fun onInitializationComplete(isSuccessful: Boolean) {
                    if (isSuccessful) launchFragment()
                }
            })
            .build()
    }

    private fun launchFragment() {
        activityTestRule.scenario.onActivity {
            ReviewRelic.showSheet(
                transactionId = transactionId,
                thankYouMessage = thankYouMessage,
                fragmentManager = it.supportFragmentManager
            )
        }
    }

    @Test
    fun testSubmitWithoutReviewSelect() {
        Thread.sleep(5000)
        onView(withId(R.id.textViewTitle)).check(matches(withText(ReviewRelic.reviewRelicSettings?.name)))
        onView(withId(R.id.recyclerViewItems)).check(
            RecyclerViewItemCountAssertion(
                ReviewRelic.reviewRelicSettings?.reviewSettings?.count() ?: 0
            )
        )
        if (ReviewRelic.reviewRelicSettings?.settings?.shouldShowImages == true)
            onView(
                RecyclerViewMatcher(R.id.recyclerViewItems)
                    .atPositionOnView(
                        (0..(ReviewRelic.reviewRelicSettings?.reviewSettings?.count()
                            ?: 0)).random(),
                        R.id.imageViewReview
                    )
            )
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        else {
            onView(
                RecyclerViewMatcher(R.id.recyclerViewItems)
                    .atPositionOnView(
                        (0..(ReviewRelic.reviewRelicSettings?.reviewSettings?.count()
                            ?: 0)).random(),
                        R.id.textViewItem
                    )
            )
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
        onView(withId(R.id.editTextReview)).perform(betterScrollTo(), typeText("Some Text"))
        onView(isRoot()).perform(closeSoftKeyboard())
        onView(withId(R.id.ivArrowDown)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.buttonSubmitReview)).perform(betterScrollTo(), click())
        onView(withId(R.id.buttonSubmitReview)).check(matches(not(isEnabled())))
        Thread.sleep(2000)
    }

    @Test
    fun testSubmitWithReviewSelect() {
        Thread.sleep(5000)
        onView(withId(R.id.textViewTitle)).check(matches(withText(ReviewRelic.reviewRelicSettings?.name)))
        onView(withId(R.id.recyclerViewItems)).check(
            RecyclerViewItemCountAssertion(
                ReviewRelic.reviewRelicSettings?.reviewSettings?.count() ?: 0
            )
        )
        onView(withId(R.id.recyclerViewItems)).perform(
            actionOnItemAtPosition<DataBoundViewHolder<ReviewItemLayoutBinding>>(
                (0..(ReviewRelic.reviewRelicSettings?.reviewSettings?.count() ?: 0)).random(),
                RecyclerViewItemClickAction.clickChildViewWithId(R.id.textViewItem)
            )
        )
        onView(withId(R.id.editTextReview)).perform(betterScrollTo(), typeText("Some Text"))
        onView(isRoot()).perform(closeSoftKeyboard())
        onView(withId(R.id.buttonSubmitReview)).perform(betterScrollTo(), click())
        onView(withId(R.id.buttonSubmitReview)).check(matches(isEnabled()))
        Thread.sleep(3000)
        onView(withId(R.id.groupThankYou)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.textViewThankYou)).check(matches(withText(thankYouMessage)))
    }


    // scroll-to action that also works with NestedScrollViews
    class BetterScrollToAction : ViewAction by ScrollToAction() {

        override fun getDescription(): String {
            return "Not yet implemented"
        }

        override fun getConstraints(): Matcher<View> {
            return Matchers.allOf(
                withEffectiveVisibility(Visibility.VISIBLE),
                isDescendantOfA(
                    Matchers.anyOf(
                        isAssignableFrom(NestedScrollView::class.java)
                    )
                )
            )
        }
    }

    // convenience method
    private fun betterScrollTo(): ViewAction {
        return actionWithAssertions(BetterScrollToAction())
    }

    object RecyclerViewItemClickAction {
        fun clickChildViewWithId(id: Int): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View>? {
                    return null
                }

                override fun getDescription(): String {
                    return "Click on a child view with specified id."
                }

                override fun perform(uiController: UiController?, view: View) {
                    val v = view.findViewById<View>(id)
                    v.performClick()
                }
            }
        }
    }

}