package com.jayner.githubrepos.trending

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.jayner.githubrepos.MainActivity_
import com.jayner.githubrepos.idlingresource.EspressoTestingIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class TrendingReposFragmentTest {

    private val ITEM_BELOW_THE_FOLD = 0

    @Before
    fun launchActivity() {
        val activityScenario = ActivityScenario.launch(MainActivity_::class.java)
        // let espresso know to synchronize with background tasks
        IdlingRegistry.getInstance().register(EspressoTestingIdlingResource.getInstance().getIdlingResource())
    }

    @Test
    fun scrollToItemBelowFold_checkItsText() {
        onView(withId(com.jayner.githubrepos.R.id.repo_list)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(com.jayner.githubrepos.R.id.text_error)).check(ViewAssertions.matches(withEffectiveVisibility(Visibility.GONE)))

        // First scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(com.jayner.githubrepos.R.id.repo_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RepoListAdapter.RepoViewHolder>(ITEM_BELOW_THE_FOLD, click()))

        // Match the text in an item below the fold and check that it's displayed.
        val itemElementText = "kotlin" // This is assuming that the first item in the list is the kotlin repo
        onView(withText(itemElementText)).check(ViewAssertions.matches(isDisplayed()))
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoTestingIdlingResource.getInstance().getIdlingResource())
    }
}