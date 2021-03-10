package com.reviewrelic.reviewrelic


import android.util.EventLogTags
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.accessibility_action_clickable_span), withText("Get"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.click), withText("Click"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        val appCompatImageView = onView(
            allOf(
                withId(R.id.btnCross),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nestedScroll),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.click), withText("Click"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())

        val imageView = onView(
            allOf(
                withId(R.id.ivArrowDown),
                withParent(withParent(withId(R.id.design_bottom_sheet))),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                val parent = view?.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }

        }
    }
}
