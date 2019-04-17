package com.jayner.githubrepos.trending

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jayner.githubrepos.data.GitHubRepository
import com.jayner.githubrepos.data.TrendingReposResponse
import com.jayner.githubrepos.test.RxSchedulerRule
import com.jayner.githubrepos.utils.TestUtils
import com.jraska.livedata.test
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit

class TrendingReposViewModelTest {

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    @Mock
    lateinit var mockGitHubRepository: GitHubRepository

    private lateinit var viewModel: TrendingReposViewModel
    private lateinit var trendingReposResponse: TrendingReposResponse

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        trendingReposResponse = TestUtils.loadJson("mock/trending_repos.json", TrendingReposResponse::class.java)

        viewModel = TrendingReposViewModel(mockGitHubRepository)
    }

    /**
     * Asserting that the LiveData values have no value at the start (before a repos retrieval is performed).
     */
    @Test
    fun testNull() {
        viewModel.getRepos().test().assertNoValue()
        viewModel.hasError().test().assertNoValue()
        viewModel.isLoading().test().assertNoValue()

        Mockito.verify(mockGitHubRepository, Mockito.never()).getTrendingRepos()
    }

    /**
     * If we observe after retrieving the repo list, the observers only receive one value, which is the state after the
     * retrieval is successfully complete.
     */
    @Test
    fun testObserveAfterFetchRepos() {
        Mockito.`when`(mockGitHubRepository.getTrendingRepos()).thenReturn(Single.just(trendingReposResponse.items))

        viewModel.start()

        var loadingObserver = viewModel.isLoading().test()
        var reposObserver = viewModel.getRepos().test()
        var errorObserver = viewModel.hasError().test()

        loadingObserver.assertHasValue().assertValueHistory(false).assertHistorySize(1).assertValue(false)
        reposObserver.assertHasValue().assertHistorySize(1).assertValue(trendingReposResponse.items)
        errorObserver.assertHasValue().assertHistorySize(1).assertValue(false)

        Mockito.verify(mockGitHubRepository, Mockito.only()).getTrendingRepos()
    }

    /**
     * If we observer before we retrieve the repo list, then loading is called twice - once before the retrieval
     * and once after the retrieval.
     */
    @Test
    fun testObserverBeforeFetchRepos() {
        Mockito.`when`(mockGitHubRepository.getTrendingRepos()).thenReturn(Single.just(trendingReposResponse.items))

        var loadingObserver = viewModel.isLoading().test()
        var reposObserver = viewModel.getRepos().test()
        var errorObserver = viewModel.hasError().test()

        viewModel.start()

        loadingObserver.assertHasValue().assertValueHistory(true, false).assertHistorySize(2).assertValue(false)
        reposObserver.assertHasValue().assertHistorySize(1).assertValue(trendingReposResponse.items)
        errorObserver.assertHasValue().assertHistorySize(1).assertValue(false)

        Mockito.verify(mockGitHubRepository, Mockito.only()).getTrendingRepos()
    }

    /**
     * Testing that we only fetch the results the first time that start is called, if the repos have already been
     * populated.
     */
    @Test
    fun testCallStartTwice() {
        Mockito.`when`(mockGitHubRepository.getTrendingRepos()).thenReturn(Single.just(trendingReposResponse.items))

        var loadingObserver = viewModel.isLoading().test()
        var reposObserver = viewModel.getRepos().test()
        var errorObserver = viewModel.hasError().test()

        viewModel.start()
        viewModel.start()

        loadingObserver.assertHasValue().assertValueHistory(true, false).assertHistorySize(2).assertValue(false)
        reposObserver.assertHasValue().assertHistorySize(1).assertValue(trendingReposResponse.items)
        errorObserver.assertHasValue().assertHistorySize(1).assertValue(false)

        Mockito.verify(mockGitHubRepository, Mockito.only()).getTrendingRepos()
    }

    /**
     * Test the liveData values when an error occurred furing the loading of the repos.
     */
    @Test
    fun testError() {
        Mockito.`when`(mockGitHubRepository.getTrendingRepos()).thenReturn(Single.error(Exception("Test Exception")))

        var loadingObserver = viewModel.isLoading().test()
        var reposObserver = viewModel.getRepos().test()
        var errorObserver = viewModel.hasError().test()

        viewModel.start()

        loadingObserver.assertHasValue().assertValueHistory(true, false).assertHistorySize(2).assertValue(false)
        reposObserver.assertNoValue()
        errorObserver.assertHasValue().assertHistorySize(1).assertValue(true)

        Mockito.verify(mockGitHubRepository, Mockito.only()).getTrendingRepos()
    }
}